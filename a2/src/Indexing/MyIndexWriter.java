package Indexing;

import Classes.Path;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyIndexWriter {
    // I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
    private BufferedWriter writer;
    private BufferedWriter docIdWriter;
    private int docId;
    private Map<String, StringBuilder> indexes;

    public MyIndexWriter(String type) throws IOException {
        // This constructor should initiate the FileWriter to output your index files
        // remember to close files if you finish writing the index
        docId = -1;
        indexes = new HashMap<>();

        String dir;
        if (type.equals("trectext"))
            dir = Path.IndexTextDir;
        else
            dir = Path.IndexWebDir;
        writer = new BufferedWriter(new FileWriter(createFile(dir + "index"), false));
        docIdWriter = new BufferedWriter(new FileWriter(createFile(dir + "docId"), false));
    }

    public void IndexADocument(String docno, String content) throws IOException {
        // you are strongly suggested to build the index by installments
        // you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader

        // store docId
        docIdWriter.write(++docId + ":" + docno);
        docIdWriter.newLine();

        Map<String, Integer> countMap = new HashMap<>();
        for (String word : content.split(" ")) {
            if (countMap.containsKey(word))
                countMap.put(word, countMap.get(word) + 1);
            else
                countMap.put(word, 1);
        }
        for (String word : countMap.keySet()) {
            if (indexes.containsKey(word)) {
                indexes.get(word).append(docId).append(":").append(countMap.get(word)).append(",");
            } else {
                indexes.put(word, new StringBuilder().append(docId).append(":").append(countMap.get(word)).append(","));
            }
        }
    }

    public void Close() throws IOException {
        // close the index writer, and you should output all the buffered content (if any).
        // if you write your index into several files, you need to fuse them here.
        for (String word : indexes.keySet()) {
            writer.write(word + "::" + indexes.get(word));
            writer.newLine();
        }

        docIdWriter.flush();
        docIdWriter.close();
        writer.flush();
        writer.close();
    }

    private File createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            File dir = new File(file.getParent());
            if (!dir.exists() && !dir.mkdirs())
                throw new IOException("Failed to create directory");
            if (!file.createNewFile())
                throw new IOException("Failed to create file: " + filePath);
        }
        return file;
    }

}
