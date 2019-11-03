package xExtraClass;

/**
 * Last modification 10 March 2013
 */

import java.io.Serializable;
import java.util.Arrays;

import shared.Constant;
import shared.Word;
import shared.WordList;

public class CaseDescription_2014Version implements Serializable {
	private static final long serialVersionUID = -9139701688241540085L;

	private final int featuresSize = Constant.featureSize;
	private double[] features = new double[featuresSize];
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

	public CaseDescription_2014Version() {
	}

	public CaseDescription_2014Version(double[] features) {
		this.features = Arrays.copyOf(features, featuresSize);
	}

	public CaseDescription_2014Version(OldDocument document) {
		setStatisticalFeaturesOfDocument(document);
		setWritingStyleFeaturesOfDocument(document);
	}

	private void setStatisticalFeaturesOfDocument(OldDocument document) {
		WordList wordsList = document.getWordsList();
		int totalTokensCounts = 0;
		for (Word word : wordsList.getWords()) {
			totalTokensCounts += (double) word.getCount();
			computePOSFrequencyFeatures(word);
		}
		features[TOTAL_WORDS_INDEX] = document.getWordsList()
				.getWords().size();
		features[TOTAL_TOKENS_INDEX] = totalTokensCounts;
		features[TOTAL_SENTENCES_INDEX] = document.getTotalSentencesCount();
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

	private void setWritingStyleFeaturesOfDocument(OldDocument document) {
		WordList wordList = document.getWordsList();
		features[SPACE_RATIO_INDEX] = document.getTotalWhitespacesCount()
				/ document.getTotlaCharactersCount();
		features[STOPWORDS_RATIO_INDEX] = countStopWordsRatio(wordList,
				features[TOTAL_TOKENS_INDEX]);
		features[AVERAGE_SYLLABLES_COUNT_INDEX] = countAverageSyllableCounts(
				wordList, features[TOTAL_TOKENS_INDEX]);
		features[MONOSYLLABLE_RATIO_INDEX] = countingMonoSyllableRatio(
				wordList, features[TOTAL_TOKENS_INDEX]);
		features[WORD_TO_TOKEN_RATIO_INDEX] = features[TOTAL_WORDS_INDEX]
				/ features[TOTAL_TOKENS_INDEX];
		features[UNIQUE_WORDS_RATIO_INDEX] = countingUniqueWordsRatio(wordList,
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

	private double countingMonoSyllableRatio(WordList wordList,
			double totalTokenCounts) {
		int totalMonoSyllableCounts = 0;
		for (Word word : wordList.getWords()) {
			totalMonoSyllableCounts += (word.getNumberSyllables() == 1) ? word
					.getCount() : 0;
		}
		return totalMonoSyllableCounts / totalTokenCounts;
	}

	private double countingUniqueWordsRatio(WordList wordList,
			double totalWordCount) {
		int totalUniqueWordCounts = 0;
		for (Word word : wordList.getWords()) {
			totalUniqueWordCounts += (word.isUnique()) ? 1 : 0;
		}
		return totalUniqueWordCounts / totalWordCount;
	}

	public void setFeatures(double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}

	public double[] getFeatures() {
		return features;
	}

}
