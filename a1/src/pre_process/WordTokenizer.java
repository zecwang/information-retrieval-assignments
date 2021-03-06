package pre_process;

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
        boolean htmlTagHold = false; // true if it is a html tag
        for (int i = 0, len = texts.length(); i < len; i++) {
            if (htmlTagHold) { // htmlTagHold = true, skip each character until we get a '>'
                if (texts.charAt(i) == '>')
                    htmlTagHold = false;
                continue;
            }

            // htmlTagHold = false
            if (texts.charAt(i) == '<')
                htmlTagHold = true; // new tag appears
            else if (!Character.isLetter(texts.charAt(i)) && texts.charAt(i) != 39 && texts.charAt(i) < 128) { // if current character is a punctuation mark or a space or '\n'
                if (word.length() != 0) {
                    words.add(word.toString());
                    word = new StringBuilder();
                }
            } else // [a-zA-Z] or some special characters
                word.append(texts.charAt(i));
        }

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
