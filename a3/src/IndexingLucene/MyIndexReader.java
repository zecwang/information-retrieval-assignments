package IndexingLucene;

import Classes.Path;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * A class for reading your index.
 */
public class MyIndexReader {
	protected File dir;
	private Directory directory;
	private DirectoryReader ireader;
	private IndexSearcher isearcher;
	private long collectionLength;
	
	public MyIndexReader(String dataType) throws IOException {
		if (dataType.equals("trectext")) {
			directory = FSDirectory.open(Paths.get(Classes.Path.IndexTextDir));
		} else {
			directory = FSDirectory.open(Paths.get(Classes.Path.IndexWebDir));
		}
		ireader = DirectoryReader.open(directory);
		isearcher = new IndexSearcher(ireader);
		
		// calculate collection length
		BufferedReader reader = new BufferedReader(new FileReader(new File(Path.ResultHM1 + dataType)));
		int totalDocs = 0;
		while (reader.readLine() != null) {
			reader.readLine();
			totalDocs++;
		}
		reader.close();
		collectionLength = 0;
		for (int i = 0; i < totalDocs; i++) {
			collectionLength += docLength(i);
		}
	}
	
	public long getCollectionLength() {
		return collectionLength;
	}
	
	/**
	 * Get the (non-negative) integer docid for the requested docno.
	 * If -1 returned, it indicates the requested docno does not exist in the index.
	 *
	 * @param docno
	 * @return
	 * @throws IOException
	 */
	public int getDocid(String docno) throws IOException {
		// you should implement this method.
		Query query = new TermQuery(new Term("DOCNO", docno));
		TopDocs tops = isearcher.search(query, 1);
		return tops.scoreDocs[0].doc;
	}
	
	/**
	 * Retrive the docno for the integer docid.
	 *
	 * @param docid
	 * @return
	 * @throws IOException
	 */
	public String getDocno(int docid) throws IOException {
		// you should implement this method.
		Document doc = ireader.document(docid);
		return (doc == null) ? null : doc.get("DOCNO");
	}
	
	
	/**
	 * Get the posting list for the requested token.
	 * <p>
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 * <p>
	 * [docid]		[freq]
	 * 1			3
	 * 5			7
	 * 9			1
	 * 13			9
	 * <p>
	 * ...
	 * <p>
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * <p>
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * <p>
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest.
	 *
	 * @param token
	 * @return
	 */
	public int[][] getPostingList(String token) throws IOException {
		// you should implement this method.
		Term tm = new Term("CONTENT", token);
		int df = ireader.docFreq(tm);
		if (df == 0)
			return null;
		Query query = new TermQuery(tm);
		TopDocs tops = isearcher.search(query, df);
		ScoreDoc[] scoreDoc = tops.scoreDocs;
		int[][] posting = new int[df][];
		int ix = 0;
		Terms vector;
		TermsEnum termsEnum;
		BytesRef text;
		for (ScoreDoc score : scoreDoc) {
			int id = score.doc;
			int freq = 0;
			vector = ireader.getTermVector(id, "CONTENT");
			termsEnum = vector.iterator();
			while ((text = termsEnum.next()) != null) {
				if (text.utf8ToString().equals(token))
					freq += (int) termsEnum.totalTermFreq();
			}
			posting[ix] = new int[]{id, freq};
			ix++;
		}
		return posting;
	}
	
	/**
	 * Return the number of documents that contains the token.
	 *
	 * @param token
	 * @return
	 */
	public int DocFreq(String token) throws IOException {
		Term tm = new Term("CONTENT", token);
		int df = ireader.docFreq(tm);
		return df;
	}
	
	/**
	 * Return the total number of times the token appears in the collection.
	 *
	 * @param token
	 * @return
	 */
	public long CollectionFreq(String token) throws IOException {
		// you should implement this method.
		Term tm = new Term("CONTENT", token);
		long ctf = ireader.totalTermFreq(tm);
		return ctf;
	}
	
	/**
	 * Get the length of the requested document.
	 *
	 * @param docid
	 * @return
	 * @throws IOException
	 */
	public int docLength(int docid) throws IOException {
		int doc_length = 0;
		Terms vector = ireader.getTermVector(docid, "CONTENT");
		try {
			TermsEnum termsEnum = vector.iterator();
			BytesRef text;
			while ((text = termsEnum.next()) != null) {
				doc_length += (int) termsEnum.totalTermFreq();
			}
			return doc_length;
		} catch (NullPointerException e) {
			return 0;
		}
	}
	
	public void close() throws IOException {
		// you should implement this method when necessary
		ireader.close();
		directory.close();
	}
	
}
