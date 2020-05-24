package Indexing;

import Classes.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PreProcessedCorpusReader {
    private BufferedReader reader;


    public PreProcessedCorpusReader(String type) throws IOException {
        // This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
        // You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
        // Close the file when you do not use it any more
        File file = new File(Path.ResultHM1 + type);
        reader = new BufferedReader(new FileReader(file));
    }


    public Map<String, String> NextDocument() throws IOException {
        // read a line for docNo, put into the map with <"DOCNO", docNo>
        // read another line for the content , put into the map with <"CONTENT", content>
        Map<String, String> doc = new HashMap<>();
        String docNo;
        while ((docNo = reader.readLine()) != null) {
            String content = reader.readLine();
            doc.put("DOCNO", docNo);
            doc.put("CONTENT", content);
            return doc;
        }

        reader.close();

        return null;
    }

}
