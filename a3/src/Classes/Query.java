package Classes;

import java.util.ArrayList;

public class Query {
	//you can modify this class
	
	private String topicId;
	private String queryContent;
	private ArrayList<String> queryTerms;
	
	public Query(String topicId, String queryContent, ArrayList<String> queryTerms) {
		this.topicId = topicId;
		this.queryContent = queryContent;
		this.queryTerms = queryTerms;
	}
	
	public ArrayList<String> getQueryTerms() {
		return queryTerms;
	}
	
	public void setQueryTerms(ArrayList<String> queryTerms) {
		this.queryTerms = queryTerms;
	}
	
	public String GetQueryContent() {
		return queryContent;
	}
	
	public String GetTopicId() {
		return topicId;
	}
	
	public void SetQueryContent(String content) {
		queryContent = content;
	}
	
	public void SetTopicId(String id) {
		topicId = id;
	}
}
