package pre_process;

import classes.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is for INFSCI 2140 in 2019
 */
public class TrecwebCollection implements DocumentCollection {
    // Essential private methods or variables can be added.
    private BufferedReader reader;

    // YOU SHOULD IMPLEMENT THIS METHOD.
    public TrecwebCollection() throws IOException {
        // 1. Open the file in Path.DataWebDir.
        // 2. Make preparation for function nextDocument().
        // NT: you cannot load the whole corpus into memory!!

        File file = new File(Path.DataWebDir);
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // YOU SHOULD IMPLEMENT THIS METHOD.
    public Map<String, Object> nextDocument() throws IOException {
        // 1. When called, this API processes one document from corpus, and returns its
        // doc number and content.
        // 2. When no document left, return null, and close the file.
        // 3. the HTML tags should be removed in document content.

        StringBuilder content;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("<DOCNO")) {
                String docNo = line.substring(7, line.length() - 8);
                content = new StringBuilder();
                boolean isContent = false;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("</DOC>")) break;
                    if (isContent) {
                        content.append(line);
                        content.append(" ");
                    }
                    if (line.startsWith("</DOCHDR")) isContent = true; // If line starts with "</DOCHDR", then next line would be the content.
                }
                Map<String, Object> doc = new HashMap<>();
                doc.put(docNo, content.toString());

                return doc;
            }
        }

        reader.close();

        return null;
    }

}
