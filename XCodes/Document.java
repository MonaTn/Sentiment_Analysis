package shared;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.util.StringUtils;

//import lexicon.Lexicon;

public class Document {
	
	private String 	name;
	private String 	path;
	private boolean polarity;
//	private double 	score; //use in test process 
	private int 	totalSentencesCount;
	private double 	totalWhitespacesCount; 
	private double 	totlaCharactersCount;
	private CaseDescription caseDescription;
	private List<Word> wordsList = new ArrayList<Word>();
//	private List<Class<? extends Lexicon>> caseSolution = new ArrayList<Class<? extends Lexicon>>(); //use in test process
		
	public Document (String name , String path, boolean polarity ) throws ClassNotFoundException, IOException {
		this.name = name;
		this.path = path;
		this.polarity = polarity;
		countWhiteSpacesAndCharacters();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getPolarity() {
		return polarity;
	}
	public void setPolarity(boolean polarity) {
		this.polarity = polarity;
	}
	
//	public double getScore() {
//		return score;
//	}
//	public void setScore(double score) {
//		this.score = score;
//	}
	
	public CaseDescription getCaseDescription() {
		return caseDescription;
	}
	public void setCaseDescription(CaseDescription caseDescription) {
		this.caseDescription = caseDescription;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String docpath) {
		this.path = docpath;
	}

	public List<Word> getWordsList() {
		return wordsList;
	}

	public void setWordsList(List<Word> wordsMap) {
		this.wordsList = wordsMap;
	}

	public int getTotalSentencesCount() {
		return totalSentencesCount;
	}

	public void setTotalSentencesCount(int totalSentencesCount) {
		this.totalSentencesCount = totalSentencesCount;
	}

	public double getTotalWhitespacesCount() {
		return totalWhitespacesCount;
	}

	public void setTotalWhitespacesCount(double totalWhitespacesCount) {
		this.totalWhitespacesCount = totalWhitespacesCount;
	}

	public double getTotlaCharactersCount() {
		return totlaCharactersCount;
	}

	public void setTotlaCharactersCount(double totlaCharactersCount) {
		this.totlaCharactersCount = totlaCharactersCount;
	}
	
	public String getAbsoluteFile() {
		return this.getPath() + "/" + this.getName();
	}
	
	public void countWhiteSpacesAndCharacters()
			throws IOException {
		Scanner scanner = new Scanner(new File(this.getAbsoluteFile()));
		int totalWhitespacesCount = 0;
		int totlaCharactersCount = 0;
		String whitSpace = " ";
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			totalWhitespacesCount += StringUtils.countOccurrencesOf(line,
					whitSpace);
			totlaCharactersCount += line.length();
		}
		this.setTotalWhitespacesCount(totalWhitespacesCount);
		this.setTotlaCharactersCount(totlaCharactersCount);
		scanner.close();
	}
	
}
