package xTestUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import shared.Constant;
import shared.Document;
import shared.EnglishSyllableCounter;
import shared.StopWords;
import shared.Word;

public class CaseDescription_15112012 implements Serializable {
	private static final long serialVersionUID = -9139701688241540085L;

	private double[] features = new double[Constant.getFeatureSize()];

	public CaseDescription_15112012() {
	}

	public CaseDescription_15112012(double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}

	public CaseDescription_15112012(Document document) {
		List<Word> wordsList = document.getWordsList();
		int totalStopWordCounts = 0;
		int totalUniqueWordCounts = 0;
		for (Word word : wordsList) {
			features[TOTAL_TOKENS_INDEX] += (double) word.getCount();
//			totalStopWordCounts += countingStopWords(word);
//			totalUniqueWordCounts += countingUniqueWords (word);
			computePOSFrequencyFeatures(word);
		}
		features[TOTAL_WORDS_INDEX] = document.getWordsList().size();
		features[TOTAL_SENTENCES_INDEX] = document.getTotalSentenceCounts();
		features[SPACE_RATIO_INDEX] = (double) document.getTotalWhitespaceCounts()
				/ (double) document.getTotlaCharacterCounts();
		features[AVERAGE_SENTENCE_SIZE_INDEX] += features[TOTAL_TOKENS_INDEX]
				/ features[TOTAL_SENTENCES_INDEX];
		features[STOPWORDS_RATIO_INDEX] = computeStopWordsCount(wordsList)
				/ features[TOTAL_TOKENS_INDEX];
		features[WORD_TO_TOKEN_RATIO_INDEX] = features[TOTAL_WORDS_INDEX]
				/ features[TOTAL_TOKENS_INDEX];
		features[UNIQUE_WORDS_RATIO_INDEX] = computeUniqueWordsCount(wordsList)
				/ features[TOTAL_WORDS_INDEX];
		computeMonoSyllableRetioAndAverageSyllableCount(wordsList);
	}

	private double computeUniqueWordsCount(List<Word> wordList) {
		Predicate<Word> isUnique = new Predicate<Word>() {
			@Override
			public boolean apply(Word word) {
				return (word.getCount() == 1);
			}
		};
		Collection<Word> filteredUniqueWords = Collections2.filter(wordList,
				isUnique);
		return (double) filteredUniqueWords.size();
	}

	private double computeStopWordsCount(List<Word> wordList) {
		double stopWordCount = 0;
		Predicate<Word> isStopWord = new Predicate<Word>() {
			@Override
			public boolean apply(Word word) {
				return StopWords.isStopWord(word.getWord());
			}
		};
		Collection<Word> filteredStopWords = Collections2.filter(wordList,
				isStopWord);
		for (Word word : filteredStopWords) {
			stopWordCount += word.count;
		}
		return stopWordCount;
	}

	private void computeMonoSyllableRetioAndAverageSyllableCount(
			List<Word> wordsList) {
		double totalSyllableCount = 0;
		double monoSyllableCount = 0;
		EnglishSyllableCounter syllableCounter = new EnglishSyllableCounter();
		for (Word word : wordsList) {
			int syllableCount = syllableCounter.countSyllables(word.getWord());
			totalSyllableCount += (double) (syllableCount * word.getCount());
			if (syllableCount == 1) {
				monoSyllableCount += word.getCount();
			}
		}
		features[AVERAGE_SYLLABLES_COUNT_INDEX] = totalSyllableCount
				/ features[TOTAL_TOKENS_INDEX];
		features[MONOSYLLABLE_RATIO_INDEX] = monoSyllableCount
				/ features[TOTAL_TOKENS_INDEX];
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

	public void setFeatures(double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}

	public double[] getFeatures() {
		return features;
	}
	
	private final static int TOTAL_WORDS_INDEX = 0;
	private final static int TOTAL_TOKENS_INDEX = 1;
	private final static int TOTAL_SENTENCES_INDEX = 2;
	private final static int AVERAGE_SENTENCE_SIZE_INDEX = 3;
	private final static int SPACE_RATIO_INDEX = 4;
	private final static int STOPWORDS_RATIO_INDEX = 5;
	private final static int AVERAGE_SYLLABLES_COUNT_INDEX = 6;
	private final static int MONOSYLLABLE_RATIO_INDEX = 7;
	private final static int WORD_TO_TOKEN_RATIO_INDEX = 8;
	private final static int UNIQUE_WORDS_RATIO_INDEX = 9;
	private final static int POS_VERBS_INDEX = 10;
	private final static int POS_ADJECTIVE_INDEX = 11;
	private final static int POS_ADVERB_INDEX = 12;
	private final static int POS_NOUNS_INDEX = 13;
	private final static int POS_PUNCTUATION_INDEX = 14;
	private final static int POS_CONJUNCTION_INDEX = 15;
	private final static int POS_INTERJECTION_INDEX = 16;

}
