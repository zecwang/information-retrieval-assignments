package Indexing;

import Classes.Path;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class MyIndexReader {
    private BufferedReader docIdReader;
    private String dir;
    private Map<String, String> cache;

    public MyIndexReader(String type) throws IOException {
        //read the index files you generated in task 1
        //remember to close them when you finish using them
        //use appropriate structure to store your index
        cache = new HashMap<>();
        if (type.equals("trectext"))
            dir = Path.IndexTextDir;
        else
            dir = Path.IndexWebDir;
    }

    //get the non-negative integer dociId for the requested docNo
    //If the requested docno does not exist in the index, return -1
    public int GetDocid(String docno) {
        try {
            docIdReader = new BufferedReader(new FileReader(new File(dir + "docId")));
            String line;
            while ((line = docIdReader.readLine()) != null) {
                if (line.endsWith(docno)) {
                    docIdReader.close();
                    return Integer.parseInt(line.split(":")[0]);
                }
            }
            docIdReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Retrieve the docno for the integer docid
    public String GetDocno(int docid) {
        try {
            docIdReader = new BufferedReader(new FileReader(new File(dir + "docId")));
            String line;
            while ((line = docIdReader.readLine()) != null) {
                if (line.startsWith(docid + ":")) {
                    docIdReader.close();
                    return line.split(":")[1];
                }
            }
            docIdReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
    public int[][] GetPostingList(String token) throws IOException {
        String posting = getPosting(token);
        if (posting != null) {
            String[] docs = posting.split(",");
            int[][] result = new int[docs.length][2];
            int i = 0;
            for (String doc : docs) {
                int docId = Integer.parseInt(doc.split(":")[0]);
                int freq = Integer.parseInt(doc.split(":")[1]);
                result[i][0] = docId;
                result[i++][1] = freq;
            }
            return result;
        } else
            return null;
    }

    // Return the number of documents that contains the token.
    public int GetDocFreq(String token) throws IOException {
        String posting = getPosting(token);
        if (posting != null) {
            int freq = 0;
            for (int i = 0, len = posting.length(); i < len; i++) {
                if (posting.charAt(i) == ',')
                    freq++;
            }
            return freq;
        } else
            return 0;
    }

    // Return the total number of times the token appears in the collection.
    public long GetCollectionFreq(String token) throws IOException {
        String posting = getPosting(token);
        if (posting != null) {
            int total = 0;
            for (String doc : posting.split(",")) {
                total += Integer.parseInt(doc.split(":")[1]);
            }
            return total;
        } else
            return 0;
    }

    public void Close() throws IOException {
    }

    private String getPosting(String token) throws IOException {
        if (cache.containsKey(token)) return cache.get(token);
        //you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
        BufferedReader reader = new BufferedReader(new FileReader(new File(dir + "index")));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(token + ":")) {
                String posting = line.split("::")[1];
                cache.put(token, posting);
                reader.close();
                return posting;
            }
        }
        reader.close();
        return null;
    }

}