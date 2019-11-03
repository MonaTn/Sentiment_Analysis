package shared;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import shared.EnglishSyllableCounter;
import shared.StopWords;

public class CaseDescription implements Serializable {

	private static final long serialVersionUID = -9139701688241540085L;
	private  double[] features = new double[20];
	private  double   totalSyllableCount = 0;

	private final static int TOTAL_WORDS_INDEX = 0;
	private final static int TOTAL_TOKENS_INDEX = 1;
	private final static int TOTAL_SENTENCES_INDEX = 2;
	private final static int AVERAGE_SENTENCE_SIZE_INDEX = 3;
	private final static int SPACE_RATIO_INDEX = 4; // ?
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

	public CaseDescription() {
	}

	public CaseDescription(double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}


	public void setFeatures(Document document) {
		List<Word> wordsList = document.getWordsList();
		computeWritingStyleFeaturesOfDocument (wordsList);
		this.setTotalSentens((double) document.getTotalSentencesNumbers());
		this.setTotalWords((double) wordsList.size());
		this.setAverageSentencesSize(features [TOTAL_TOKENS_INDEX]
				/ (double) document.getTotalSentencesNumbers());
		this.setStopWordsRatio(features[STOPWORDS_RATIO_INDEX]
				/ (double) wordsList.size());
		this.setAverageSyllablesCount(totalSyllableCount
				/ (double) wordsList.size());
		this.setMonosyllableRatio( features[MONOSYLLABLE_RATIO_INDEX]
				/ (double) (wordsList.size() - features[MONOSYLLABLE_RATIO_INDEX]));
		this.setWordsToTokensRatio((double) wordsList.size()
				/ features [TOTAL_TOKENS_INDEX]);
		this.setUniqueWordsRatio( features [UNIQUE_WORDS_RATIO_INDEX]
				/ (double) wordsList.size());
		System.out.println("TotalSentens : "+this.getTotalSentens());
		System.out.println("TotalWords : "+this.getTotalWords());
		System.out.println("AverageSentencesSize : "+this.getAverageSentencesSize());
		System.out.println("StopWordsRatio : "+getStopWordsRatio());
		System.out.println("AverageSyllablesCount : "+this.getAverageSyllablesCount());
		System.out.println("MonosyllableRatio : "+this.getMonosyllableRatio());
		System.out.println("WordsToTokensRatio : "+this.getWordsToTokensRatio());
		System.out.println("UniqueWordsRatio : "+this.getUniqueWordsRatio());
	}

	private void computeWritingStyleFeaturesOfDocument (List<Word> wordsList) {
		EnglishSyllableCounter syllableCounter = new EnglishSyllableCounter();
		for (Word word : wordsList) { //FIXME
			if (StopWords.isStopWord(word.getWord())) {
				features[STOPWORDS_RATIO_INDEX] += 1; 
			}
			totalSyllableCount += (double) syllableCounter.countSyllables(word.getWord());
			if (syllableCounter.countSyllables(word.getWord()) == 1) {
				features[MONOSYLLABLE_RATIO_INDEX] += 1;
				}
			if (word.getCount() == 1) {
				features [UNIQUE_WORDS_RATIO_INDEX] += 1;
			}
			features [TOTAL_TOKENS_INDEX] += (double) word.getCount();
			computePOSFrequency (word);
		}	
	}
	
	private  void computePOSFrequency(Word word) {
		if (word.getTag().equals("Verb")) {
			features[POS_VERBS_INDEX] += word.getCount();
		}
		else if (word.getTag().equals("Adjective")) {
			features[POS_ADJECTIVE_INDEX] += word.getCount();
		}
		else if (word.getTag().equals("Adverb")) {
			features[POS_ADVERB_INDEX] += word.getCount();
			}
		else if (word.getTag().equals("Noun")) {
				features[POS_NOUNS_INDEX] += word.getCount();
		}
		else if (word.getTag().equals("Punctuation")) {
			features[POS_PUNCTUATION_INDEX] += word.getCount();
		}
		else if (word.getTag().equals("Conjunction")) {
			features[POS_CONJUNCTION_INDEX] += word.getCount();
		}
		else if (word.getTag().equals("Interjection")) {
			features[POS_INTERJECTION_INDEX] += word.getCount();
		}				
	}

	public double[] getFeatures() {
		return features;
	}

	public void setFeatures(double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}

	public void setTotalWords(double totalWordsCount) {
		this.features[TOTAL_WORDS_INDEX] = totalWordsCount;
	}

	public void setTotalTokens(double totalTokensCount) {
		this.features[TOTAL_TOKENS_INDEX] = totalTokensCount;
	}

	public void setTotalSentens(double totalSentencsCount) {
		this.features[TOTAL_SENTENCES_INDEX] = totalSentencsCount;
	}

	public void setAverageSentencesSize(double setAverageSentencesSize) {
		this.features[AVERAGE_SENTENCE_SIZE_INDEX] = setAverageSentencesSize;
	}

	public void setSpaceRatio(double SpaceRatio) {
		this.features[SPACE_RATIO_INDEX] = SpaceRatio;
	}

	public void setStopWordsRatio(double StopWordsRatio) {
		this.features[STOPWORDS_RATIO_INDEX] = StopWordsRatio;
	}

	public void setAverageSyllablesCount(double AverageSyllablesCount) {
		this.features[AVERAGE_SYLLABLES_COUNT_INDEX] = AverageSyllablesCount;
	}

	public void setMonosyllableRatio(double monosyllableRatio) {
		this.features[MONOSYLLABLE_RATIO_INDEX] = monosyllableRatio;
	}

	public void setWordsToTokensRatio(double wordsToTokensRatio) {
		this.features[WORD_TO_TOKEN_RATIO_INDEX] = wordsToTokensRatio;
	}

	public void setUniqueWordsRatio(double uniqueWordsRatio) {
		this.features[UNIQUE_WORDS_RATIO_INDEX] = uniqueWordsRatio;
	}

	public void setPosVerbsFrequency(double PosVerbsCount) {
		this.features[POS_VERBS_INDEX] = PosVerbsCount;
	}

	public void setPosAdjectiveFrequency(double adjectiveCounts) {
		this.features[POS_ADJECTIVE_INDEX] = adjectiveCounts;
	}

	public void setPosAdverbFrequency(double PosAdverbCount) {
		this.features[POS_ADVERB_INDEX] = PosAdverbCount;
	}

	public void setPosNounsFrequency(double PosNounCount) {
		this.features[POS_NOUNS_INDEX] = PosNounCount;
	}

	public void setPosPonctuationFrequency(double PosPonctuationCount) {
		this.features[POS_PUNCTUATION_INDEX] = PosPonctuationCount;
	}

	public void setPosConjunctionFrequency(double PosConjunctionCount) {
		this.features[POS_CONJUNCTION_INDEX] = PosConjunctionCount;
	}

	public void setPosInterjectionFrequency(double PosInterjectionCount) {
		this.features[POS_INTERJECTION_INDEX] = PosInterjectionCount;
	}

	public double getTotalWords() {
		return features[TOTAL_WORDS_INDEX];
	}

	public double getTotalTokens() {
		return features[TOTAL_TOKENS_INDEX];
	}

	public double getTotalSentens() {
		return features[TOTAL_WORDS_INDEX];
	}

	public double getAverageSentencesSize() {
		return features[AVERAGE_SENTENCE_SIZE_INDEX];
	}

	public double getSpaceRatio() {
		return features[SPACE_RATIO_INDEX];
	}

	public double getStopWordsRatio() {
		return features[STOPWORDS_RATIO_INDEX];
	}

	public double getAverageSyllablesCount() {
		return features[AVERAGE_SYLLABLES_COUNT_INDEX];
	}

	public double getMonosyllableRatio() {
		return features[MONOSYLLABLE_RATIO_INDEX];
	}

	public double getWordsToTokensRatio() {
		return features[WORD_TO_TOKEN_RATIO_INDEX];
	}

	public double getUniqueWordsRatio() {
		return features[UNIQUE_WORDS_RATIO_INDEX];
	}

	public static int getPosVerbsFrequency() {
		return POS_VERBS_INDEX;
	}

	public static int getPosAdjectivesFrequency() {
		return POS_ADJECTIVE_INDEX;
	}

	public static int getPosAdverbsFrequency() {
		return POS_ADVERB_INDEX;
	}

	public static int getPosNounssFrequency() {
		return POS_NOUNS_INDEX;
	}

	public static int getPosPonctuationsFrequency() {
		return POS_PUNCTUATION_INDEX;
	}

	public static int getPosConjunctionsFrequency() {
		return POS_CONJUNCTION_INDEX;
	}

	public static int getPosInterjectionsFrequency() {
		return POS_INTERJECTION_INDEX;
	}

}
