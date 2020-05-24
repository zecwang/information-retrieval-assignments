package Search;

import Classes.Path;
import Classes.Query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ExtractQuery {
	
	private ArrayList<Query> topics;
	private Iterator<Query> iterator;
	
	public ExtractQuery() {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		topics = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(Path.TopicDir)));
			String line;
			String topicId = "";
			String queryContent;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("<num>")) topicId = line.split(":")[1].trim();
				else if (line.startsWith("<title>")) {
					queryContent = line.substring(7).trim();
					
					StopWordRemover stopWordRemover = new StopWordRemover();
					WordNormalizer normalizer = new WordNormalizer();
					WordTokenizer tokenizer = new WordTokenizer(queryContent);
					ArrayList<String> queryTerms = new ArrayList<>();
					String word;
					while ((word = tokenizer.nextWord()) != null) {
						word = normalizer.lowercase(word);
						if (!stopWordRemover.isStopword(word)) {
							queryTerms.add(normalizer.stem(word));
						}
					}
					
					topics.add(new Query(topicId, queryContent, queryTerms));
				}
			}
			reader.close();
			iterator = topics.iterator();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
	public Query next() {
		return iterator.next();
	}
}
