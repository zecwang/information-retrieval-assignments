package Search;

import Classes.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class StopWordRemover {
	// Essential private methods or variables can be added.
	private HashSet<String> stopWords;
	
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public StopWordRemover() {
		// Load and store the stop words from the fileinputstream with appropriate data
		// structure.
		// NT: address of stopword.txt is Path.StopwordDir
		
		stopWords = new HashSet<>();
		
		// Load and store the stop words into a hashSet
		File file = new File(Path.StopwordDir);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				stopWords.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public boolean isStopword(String word) {
		// Return true if the input word is a stopword, or false if not.
		return stopWords.contains(word);
	}
}
