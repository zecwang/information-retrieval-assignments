package Search;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;

import java.io.IOException;
import java.util.*;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	
	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
	}
	
	/**
	 * Search for the topic information.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN   The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery(Query aQuery, int TopN) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the documents based on their relevance score, from high to low
		// docId: <Term1, prob>, <Term2, prob> ...
		double u = 0.001;
		HashMap<Integer, HashMap<String, Double>> docs = new HashMap<>();
		
		// search for docs
		for (String term : aQuery.getQueryTerms()) {
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
				} else {
					docs.get(docId).put(term, p);
				}
			}
		}
		
		// calculate scores
		HashMap<Integer, Double> scoreDocs = new HashMap<>();
		for (Integer docId : docs.keySet()) {
			HashMap<String, Double> doc = docs.get(docId);
			double prob = 1;
			for (String term : aQuery.getQueryTerms()) {
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
		
		List<Document> result = new ArrayList<>();
		int i = 0;
		for (Map.Entry<Integer, Double> el : list) {
			if (++i > TopN) break;
			int docId = el.getKey();
			Document document = new Document(String.valueOf(docId), indexReader.getDocno(docId), el.getValue());
			result.add(document);
		}
		
		return result;
	}
	
}