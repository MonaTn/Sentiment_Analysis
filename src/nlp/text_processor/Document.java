package nlp.text_processor;

/**
 * Last modification 29 March 2013
 */

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import cbr.CaseDescription;

public class Document {

	private String strFileName;
	private String strPath;
	private boolean polarity;
	private int totalSentenceCounts;
	private double totalWhitespaceCounts;
	private double totalCharacterCounts;
	private CaseDescription caseDescription;
	private double[] features;
	private WordList wordsList;

	private final int TOTAL_WORDS_INDEX = 0;
	private final int TOTAL_TOKENS_INDEX = 1;
	private final int TOTAL_SENTENCES_INDEX = 2;
	private final int AVERAGE_SENTENCE_SIZE_INDEX = 3;
	private final int SPACE_RATIO_INDEX = 4;
	private final int STOPWORDS_RATIO_INDEX = 5;
	private final int AVERAGE_SYLLABLES_COUNT_INDEX = 6;
	private final int MONOSYLLABLE_RATIO_INDEX = 7;
	private final int WORD_TO_TOKEN_RATIO_INDEX = 8;
	private final int UNIQUE_WORDS_RATIO_INDEX = 9;
	private final int POS_VERBS_INDEX = 10;
	private final int POS_ADJECTIVE_INDEX = 11;
	private final int POS_ADVERB_INDEX = 12;
	private final int POS_NOUNS_INDEX = 13;
	private final int POS_PUNCTUATION_INDEX = 14;
	private final int POS_CONJUNCTION_INDEX = 15;
	private final int POS_INTERJECTION_INDEX = 16;

	public Document(String strFileName, String path, boolean polarity) {
		this.strFileName = strFileName;
		this.strPath = path;
		this.polarity = polarity;
	}

	public String getAbsoluteFileName() {
		return getPath() + "/" + getName();
	}

	public double[] getFeatures() {
		return features;
	}

	public void setFeatures(double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}

	public String getName() {
		return strFileName;
	}

	public void setName(String name) {
		this.strFileName = name;
	}

	public boolean getPolarity() {
		return polarity;
	}

	public void setPolarity(boolean polarity) {
		this.polarity = polarity;
	}

	public CaseDescription getCaseDescription() {
		return caseDescription;
	}

	public void setCaseDescription(CaseDescription caseDescription) { // **
		this.caseDescription = caseDescription;
	}

	public String getPath() {
		return strPath;
	}

	public void setPath(String docPath) {
		this.strPath = docPath;
	}

	public WordList getWordsList() {
		return wordsList;
	}

	public void setWordsList(WordList wordsMap) {
		this.wordsList = wordsMap;
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

	public CaseDescription computeCaseDescription() {
		this.features = new double[17];
		try {
			calculateWhiteSpacesAndCharacters();
			calculateStatisticalFeaturesOfDocument();
			calculateWritingStyleFeaturesOfDocument();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new CaseDescription(features);
	}

	private void calculateWhiteSpacesAndCharacters() throws IOException {
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

	private void calculateStatisticalFeaturesOfDocument() {
		WordList wordsList = getWordsList();
		int totalTokensCounts = 0;
		for (Word word : wordsList.getWords()) {
			totalTokensCounts += (double) word.getCount();
			computePOSFrequencyFeatures(word);
		}
		features[TOTAL_WORDS_INDEX] = getWordsList().getWords().size();
		features[TOTAL_TOKENS_INDEX] = totalTokensCounts;
		features[TOTAL_SENTENCES_INDEX] = totalSentenceCounts;
		features[AVERAGE_SENTENCE_SIZE_INDEX] = totalTokensCounts
				/ features[TOTAL_SENTENCES_INDEX];
	}

	private void computePOSFrequencyFeatures(Word word) {
		if (word.getTag().equals("Verb")) {
			features[POS_VERBS_INDEX] += word.getCount();
		} else if (word.getTag().equals("Adjective")) {
			features[POS_ADJECTIVE_INDEX] += word.getCount();
		} else if (word.getTag().equals("Adverb")) {
			features[POS_ADVERB_INDEX] += word.getCount();
		} else if (word.getTag().equals("Noun")) {
			features[POS_NOUNS_INDEX] += word.getCount();
		} else if (word.getTag().equals("Punctuation")) {
			features[POS_PUNCTUATION_INDEX] += word.getCount();
		} else if (word.getTag().equals("Conjunction")) {
			features[POS_CONJUNCTION_INDEX] += word.getCount();
		} else if (word.getTag().equals("Interjection")) {
			features[POS_INTERJECTION_INDEX] += word.getCount();
		}
	}

	private void calculateWritingStyleFeaturesOfDocument() {
		WordList wordList = getWordsList();
		features[SPACE_RATIO_INDEX] = getTotalWhitespacesCount()
				/ totalCharacterCounts;
		features[STOPWORDS_RATIO_INDEX] = countStopWordsRatio(wordList,
				features[TOTAL_TOKENS_INDEX]);
		features[AVERAGE_SYLLABLES_COUNT_INDEX] = countAverageSyllableCounts(
				wordList, features[TOTAL_TOKENS_INDEX]);
		features[MONOSYLLABLE_RATIO_INDEX] = counteMonoSyllableRatio(wordList,
				features[TOTAL_TOKENS_INDEX]);
		features[WORD_TO_TOKEN_RATIO_INDEX] = features[TOTAL_WORDS_INDEX]
				/ features[TOTAL_TOKENS_INDEX];
		features[UNIQUE_WORDS_RATIO_INDEX] = counteUniqueWordsRatio(wordList,
				features[TOTAL_WORDS_INDEX]);
	}

	private double countStopWordsRatio(WordList wordList,
			double totalTokenCounts) {
		int totalStopWordCounts = 0;
		for (Word word : wordList.getWords()) {
			totalStopWordCounts += (word.isStopWord()) ? word.getCount() : 0;
		}
		return (totalStopWordCounts / totalTokenCounts);
	}

	private double countAverageSyllableCounts(WordList wordList,
			double totalTokenCounts) {
		int totalSyllableCounts = 0;
		for (Word word : wordList.getWords()) {
			totalSyllableCounts += word.getNumberSyllables() * word.getCount();
		}
		return totalSyllableCounts / totalTokenCounts;
	}

	private double counteMonoSyllableRatio(WordList wordList,
			double totalTokenCounts) {
		int totalMonoSyllableCounts = 0;
		for (Word word : wordList.getWords()) {
			totalMonoSyllableCounts += (word.getNumberSyllables() == 1) ? word
					.getCount() : 0;
		}
		return totalMonoSyllableCounts / totalTokenCounts;
	}

	private double counteUniqueWordsRatio(WordList wordList,
			double totalWordCount) {
		int totalUniqueWordCounts = 0;
		for (Word word : wordList.getWords()) {
			totalUniqueWordCounts += (word.isUnique()) ? 1 : 0;
		}
		return totalUniqueWordCounts / totalWordCount;
	}

}
