package Negation;

import java.util.ArrayList;
import java.util.List;

public class PatternsList {
	private List<String> parrensList;
	
	public PatternsList() {
		parrensList = new ArrayList<String>();
	}
	
	public void add (String pattern){
		parrensList.add(pattern);
	}
	
	public List<String> getPatterns() {
		return parrensList;
	}
	
	public int getSize() {
		return parrensList.size();
	}
}
