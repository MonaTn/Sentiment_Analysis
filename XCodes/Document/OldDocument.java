package xExtraClass;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import shared.WordList;

public class OldDocument {

	private String name;
	private String path;
	private boolean polarity;
	private int totalSentenceCounts;
	private double totalWhitespaceCounts;
	private double totalCharacterCounts;
	private CaseDescription_2014Version caseDescription;
	private WordList wordsList;

	public OldDocument(String name, String path, boolean polarity)
			throws ClassNotFoundException, IOException {
		this.name = name;
		this.path = path;
		this.polarity = polarity;
		getWhiteSpacesAndCharacters();
	}

	public String getAbsoluteFileName() {
		return getPath() + "/" + getName();
	}

	public double[] getFeatures() {
		return caseDescription.getFeatures();
	}

	public void setFeatures(double[] features) {
		this.caseDescription.setFeatures(features);
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

	public CaseDescription_2014Version getCaseDescription() {
		return caseDescription;
	}

	public void setCaseDescription(CaseDescription_2014Version caseDescription) {
		this.caseDescription = caseDescription;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String docPath) {
		this.path = docPath;
	}

	public WordList getWordsList() {
		return wordsList;
	}

	public void setWordsList(WordList wordsMap) {
		this.wordsList = wordsMap;
	}

	public int getTotalSentencesCount() {
		return totalSentenceCounts;
	}

	public void setTotalSentencesCount(int totalSentenceCounts) {
		this.totalSentenceCounts = totalSentenceCounts;
	}

	public double getTotalWhitespacesCount() {
		return totalWhitespaceCounts;
	}

	public void setTotalWhitespacesCount(double totalWhitespaceCounts) {
		this.totalWhitespaceCounts = totalWhitespaceCounts;
	}

	public double getTotlaCharactersCount() {
		return totalCharacterCounts;
	}

	public void setTotlaCharactersCount(double totalCharacterCounts) {
		this.totalCharacterCounts = totalCharacterCounts;
	}

	public void getWhiteSpacesAndCharacters() throws IOException {
		Scanner scanner = new Scanner(new File(getAbsoluteFileName()));
		int totalWhitespaces = 0;
		String line = "";
		while (scanner.hasNext()) {
			line += scanner.nextLine();
		}
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '\r' || line.charAt(i) == '\t'
					|| line.charAt(i) == ' ') {
				totalWhitespaces++;
			}
		}
		this.totalWhitespaceCounts = totalWhitespaces;
		this.totalCharacterCounts = line.length();
		scanner.close();
	}

}
