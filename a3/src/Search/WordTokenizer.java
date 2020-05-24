package Search;

import java.util.ArrayList;

/**
 * This is for INFSCI 2140 in 2019
 * <p>
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	// Essential private methods or variables can be added.
	private ArrayList<String> words;
	private int index;
	
	// YOU MUST IMPLEMENT THIS METHOD.
	public WordTokenizer(String texts) {
		// Tokenize the input texts.
		
		words = new ArrayList<>();
		index = 0;
		
		StringBuilder word = new StringBuilder();
		char prev = ' ';
		for (int i = 0, len = texts.length(); i < len; i++) {
			char el = texts.charAt(i);
			if (!Character.isLetter(el) && el != '\'' && el < 128) {
				if (word.length() != 0) {
					words.add(word.toString());
					word = new StringBuilder();
				}
			} else if (el == '\'' && prev == ' ') continue;
			else word.append(el);
			
			prev = el;
		}
		if (word.length() != 0) words.add(word.toString());
	}
	
	// YOU MUST IMPLEMENT THIS METHOD.
	public String nextWord() {
		// Return the next word in the document.
		// Return null, if it is the end of the document.
		
		if (index < words.size())
			return words.get(index++); // return words.get(index), then index += 1
		
		return null;
	}
	
}
