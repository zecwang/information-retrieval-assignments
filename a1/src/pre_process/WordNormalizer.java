package pre_process;

import classes.Stemmer;

import java.util.HashMap;
import java.util.Locale;

/**
 * This is for INFSCI 2140 in 2019
 */
public class WordNormalizer {
    // Essential private methods or variables can be added.
    private HashMap<String, String> map = new HashMap<>();

    // YOU MUST IMPLEMENT THIS METHOD.
    public String lowercase(String word) {
        // Transform the word uppercase characters into lowercase.

        return word.toLowerCase(Locale.ENGLISH);
    }

    // YOU MUST IMPLEMENT THIS METHOD.
    public String stem(String word) {
        // Return the stemmed word with Stemmer in Classes package.

        if (!map.containsKey(word)) {
            Stemmer str = new Stemmer();
            char[] target = word.toCharArray();
            str.add(target, target.length);
            str.stem();
            map.put(word, str.toString());
        }
        
        return map.get(word);
    }

}
