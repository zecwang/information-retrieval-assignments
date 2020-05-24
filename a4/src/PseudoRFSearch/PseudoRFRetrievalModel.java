package PseudoRFSearch;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;

import java.util.*;

public class PseudoRFRetrievalModel {
	
	MyIndexReader indexReader;
	
	public PseudoRFRetrievalModel(MyIndexReader ixreader) {
		this.indexReader = ixreader;
	}
	
	/**
	 * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN   The maximum number of returned document
	 * @param TopK   The count of feedback documents
	 * @param alpha  parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery(Query aQuery, int TopN, int TopK, double alpha) throws Exception {
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		double u = 0.001;
		HashMap<Integer, HashMap<String, Double>> docs = new HashMap<>();
		HashMap<Integer, HashMap<String, Integer>> termFreq = new HashMap<>();
		
		String[] terms = aQuery.GetQueryContent().split(" ");
		// search for docs
		for (String term : terms) {
			int[][] posting = indexReader.getPostingList(term);
			if (posting == null) continue;
			for (int[] info : posting) {
				int docId = info[0];
				int freq = info[1];
				long collectionFreq = indexReader.CollectionFreq(term);
				double p = ((double) freq + u * collectionFreq / indexReader.getCollectionLength())
						/ (indexReader.docLength(docId) + u);
				if (!docs.containsKey(docId)) {
					HashMap<String, Double> temp = new HashMap<>();
					temp.put(term, p);
					docs.put(docId, temp);
					HashMap<String, Integer> tf = new HashMap<>();
					tf.put(term, freq);
					termFreq.put(docId, tf);
				} else {
					docs.get(docId).put(term, p);
					termFreq.get(docId).put(term, freq);
				}
			}
		}
		
		// calculate MLE scores with smoothing
		HashMap<Integer, Double> scoreDocs = new HashMap<>();
		for (Integer docId : docs.keySet()) {
			HashMap<String, Double> doc = docs.get(docId);
			double prob = 1;
			for (String term : terms) {
				if (doc.containsKey(term)) prob *= doc.get(term);
				else {
					long collectionFreq = indexReader.CollectionFreq(term);
					if (collectionFreq == 0) continue;
					double p = (u * collectionFreq / indexReader.getCollectionLength()) / (indexReader.docLength(docId) + u);
					prob *= p;
				}
			}
			scoreDocs.put(docId, prob);
		}
		
		// sort
		LinkedList<HashMap.Entry<Integer, Double>> list = new LinkedList<>(scoreDocs.entrySet());
		list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
		
		// find relevance documents
		HashMap<String, Integer> rfTermFreq = new HashMap<>();
		for (String term : terms) {
			rfTermFreq.put(term, 0);
		}
		int rfDocLength = 0;
		int i = 0;
		for (Map.Entry<Integer, Double> el : list) {
			if (++i > TopK) break;
			int docId = el.getKey();
			HashMap<String, Integer> tf = termFreq.get(docId);
			for (String term : terms) {
				if (tf.containsKey(term)) {
					rfTermFreq.put(term, rfTermFreq.get(term) + tf.get(term));
				}
			}
			rfDocLength += indexReader.docLength(docId);
		}
		
		//get P(token|feedback documents)
		HashMap<String, Double> TokenRFScore = GetTokenRFScore(terms, rfTermFreq, rfDocLength, u);
		
		// calculate PRF scores
		scoreDocs = new HashMap<>();
		for (Integer docId : docs.keySet()) {
			HashMap<String, Double> doc = docs.get(docId);
			double prob = 1;
			for (String term : terms) {
				if (doc.containsKey(term)) {
					prob *= alpha * doc.get(term) + (1 - alpha) * TokenRFScore.get(term);
				} else {
					long collectionFreq = indexReader.CollectionFreq(term);
					if (collectionFreq == 0) continue;
					double p = (u * collectionFreq / indexReader.getCollectionLength()) / (indexReader.docLength(docId) + u);
					prob *= alpha * p + (1 - alpha) * TokenRFScore.get(term);
				}
			}
			scoreDocs.put(docId, prob);
		}
		
		list = new LinkedList<>(scoreDocs.entrySet());
		list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
		
		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = new ArrayList<>();
		i = 0;
		for (Map.Entry<Integer, Double> el : list) {
			if (++i > TopN) break;
			int docId = el.getKey();
			Document document = new Document(String.valueOf(docId), indexReader.getDocno(docId), el.getValue());
			results.add(document);
		}
		
		return results;
	}
	
	public HashMap<String, Double> GetTokenRFScore(String[] terms, HashMap<String, Integer> rfTermFreq, int rfDocLength, double u) throws Exception {
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String, Double> TokenRFScore = new HashMap<String, Double>();
		for (String term : terms) {
			long collectionFreq = indexReader.CollectionFreq(term);
			double p = ((double) rfTermFreq.get(term) + u * collectionFreq / indexReader.getCollectionLength())
					/ (rfDocLength + u);
			TokenRFScore.put(term, p);
		}
		
		return TokenRFScore;
	}
	
	
}