package training;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import shared.EnglishSyllableCounter;
import shared.StopWords;


public class CaseDescription implements Serializable {

	private static final long serialVersionUID = -9139701688241540085L;
	private double[] features = new double[20];
	private final static int TOTAL_WORDS_INDEX = 0;
	private final static int TOTAL_TOKENS_INDEX = 1;
	private final static int TOTAL_SENTENCES_INDEX = 2;
	private final static int AVERAGE_SENTENCE_SIZE_INDEX = 3;
	private final static int SPACE_RATIO_INDEX = 4; //?
	private final static int STOPWORDS_RATIO_INDEX = 5;
	private final static int AVERAGE_SYLLABLES_COUNT_INDEX = 6;
	private final static int MONOSYLLABLE_RATIO_INDEX = 7;
	private final static int WORD_TO_TOKEN_RATIO_INDEX = 8;
	private final static int UNIQUE_WORDS_RATIO_INDEX = 9;
	private final static int POS_VERBS_INDEX = 10;
	private final static int POS_ADJECTIVE_INDEX = 11;
	private final static int POS_ADVERB_INDEX = 12;
	private final static int POS_NOUNS_INDEX = 13;
	private final static int POS_PONCTUATION_INDEX = 14;
	private final static int POS_CONJUNCTION_INDEX = 15;
	private final static int POS_INTERJECTION_INDEX = 16;

	public CaseDescription () {}
	public CaseDescription (double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}//? 
	
	public double[] getFeatures () {
		return features;
	}
	public void setFeatures (double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}
	public void setFeatures(Map<String, WordProperties> wordsMap , int totalSentencesCount) {
		double totalStopWordsCount = 0;
		double totalSyllableCount = 0;
		double monoSyllableCount = 0;
		double uniqueWordCount = 0;
		double totalTokensCount = 0;
		double verbCounts = 0;
		double adjectiveCounts = 0;
		double adverbCounts = 0;
		double nounCounts = 0;
		double conjunctionCounts = 0;
		double punctuationCounts = 0;
		double interjectionCounts = 0;

		EnglishSyllableCounter syllableCounter = new EnglishSyllableCounter();
		for (Map.Entry<String, WordProperties> entry : wordsMap.entrySet()) {
			totalTokensCount += (double) entry.getValue().getCount();
			if (StopWords.isStopWord(entry.getKey())) {
				totalStopWordsCount++;
			}
			totalSyllableCount += (double) syllableCounter.countSyllables(entry.getKey());
			if (syllableCounter.countSyllables(entry.getKey()) == 1) {
				monoSyllableCount++;
				}
			if (entry.getValue().getCount() == 1) {
				uniqueWordCount++;
			}
		}
		this.setTotalSentens((double) totalSentencesCount);
		this.setTotalTokens(totalTokensCount);
		this.setTotalWords((double) wordsMap.size());
		this.setAverageSentencesSize(totalTokensCount
				/ (double) totalSentencesCount);
		this.setStopWordsRatio(totalStopWordsCount
				/ (double) wordsMap.size());
		this.setAverageSyllablesCount(totalSyllableCount
				/ (double) wordsMap.size());
		this.setMonosyllableRatio( monoSyllableCount
				/ (double) (wordsMap.size() - monoSyllableCount));
		this.setWordsToTokensRatio((double) wordsMap.size()
				/ totalTokensCount);
		this.setUniqueWordsRatio( uniqueWordCount
				/ (double) wordsMap.size());
		for (WordProperties wordProperties : wordsMap.values()) {
			if (wordProperties.getTagg().contains("Verb"))
				verbCounts++;
			else if (wordProperties.getTagg().contains("Adjective"))
				adjectiveCounts++;
			else if (wordProperties.getTagg().contains("Adverb"))
				adverbCounts++;
			else if (wordProperties.getTagg().contains("Noun"))	
				nounCounts++;
			else if (wordProperties.getTagg().contains("Punctuation"))
				punctuationCounts++;
			else if (wordProperties.getTagg().contains("Conjunction"))
				conjunctionCounts++;
			else if (wordProperties.getTagg().contains("Interjection"))
				interjectionCounts++;
			else 
				continue;
			}
		this.setPosVerbsFrequency(verbCounts);
		this.setPosAdjectiveFrequency(adjectiveCounts);
		this.setPosAdverbFrequency(adverbCounts);
		this.setPosNounsFrequency(nounCounts);
		this.setPosPonctuationFrequency(punctuationCounts);
		this.setPosConjunctionFrequency(conjunctionCounts);
		this.setPosInterjectionFrequency(interjectionCounts);
	}
 
	public void setTotalWords (double totalWordsCount) {
		this.features[TOTAL_WORDS_INDEX] = totalWordsCount;
	}
	public void setTotalTokens (double totalTokensCount) {
		this.features[TOTAL_TOKENS_INDEX] = totalTokensCount;
	}
	
	public void setTotalSentens (double totalSentencsCount) {
		this.features[TOTAL_SENTENCES_INDEX] = totalSentencsCount;
	}
	
	public void setAverageSentencesSize (double setAverageSentencesSize) {
		this.features[AVERAGE_SENTENCE_SIZE_INDEX] = setAverageSentencesSize;
	}
	
	public void setSpaceRatio (double SpaceRatio) {
		this.features[SPACE_RATIO_INDEX] = SpaceRatio;
	}
	
	public void setStopWordsRatio (double StopWordsRatio) {
		this.features[STOPWORDS_RATIO_INDEX] = StopWordsRatio;
	}
	
	public void setAverageSyllablesCount (double AverageSyllablesCount) {
		this.features[AVERAGE_SYLLABLES_COUNT_INDEX] = AverageSyllablesCount;
	}
	
	public void setMonosyllableRatio (double monosyllableRatio) {
		this.features[MONOSYLLABLE_RATIO_INDEX] = monosyllableRatio;
	}
	
	public void setWordsToTokensRatio (double wordsToTokensRatio) {
		this.features[WORD_TO_TOKEN_RATIO_INDEX] = wordsToTokensRatio;
	}
	
	public void setUniqueWordsRatio (double uniqueWordsRatio) {
		this.features[UNIQUE_WORDS_RATIO_INDEX] = uniqueWordsRatio;
	}
	
	public void setPosVerbsFrequency(double PosVerbsCount) {
		this.features[POS_VERBS_INDEX] = PosVerbsCount;
	}

	public void setPosAdjectiveFrequency(double adjectiveCounts) {
		this.features[POS_ADJECTIVE_INDEX] =  adjectiveCounts;
	}

	public void setPosAdverbFrequency(double PosAdverbCount) {
		this.features[POS_ADVERB_INDEX] =  PosAdverbCount;
	}

	public void setPosNounsFrequency(double PosNounCount) {
		this.features[POS_NOUNS_INDEX] = PosNounCount;
	}

	public void setPosPonctuationFrequency(double PosPonctuationCount) {
		this.features[POS_PONCTUATION_INDEX] = PosPonctuationCount;
	}

	public void setPosConjunctionFrequency(double PosConjunctionCount) {
		this.features[POS_CONJUNCTION_INDEX]= PosConjunctionCount;
	}

	public void setPosInterjectionFrequency(double PosInterjectionCount) {
		this.features[POS_INTERJECTION_INDEX] = PosInterjectionCount;
	}
	
	public double getTotalWords () {
		return features[TOTAL_WORDS_INDEX];
	}
	public double getTotalTokens () {
		return features[TOTAL_TOKENS_INDEX];
	}
	
	public double getTotalSentens () {
		return features[TOTAL_WORDS_INDEX];
	}
	
	public double getAverageSentencesSize () {
		return features[AVERAGE_SENTENCE_SIZE_INDEX];
	}
	
	public double getSpaceRatio () {
		return features[SPACE_RATIO_INDEX];
	}
	
	public double getStopWordsRatio () {
		return features[STOPWORDS_RATIO_INDEX];
	}
	
	public double getAverageSyllablesCount () {
		return features[AVERAGE_SYLLABLES_COUNT_INDEX];
	}
	
	public double getMonosyllableRatio () {
		return features[MONOSYLLABLE_RATIO_INDEX];
	}
	
	public double getWordsToTokensRatio () {
		return features[WORD_TO_TOKEN_RATIO_INDEX];
	}
	
	public double getUniqueWordsRatio () {
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
		return POS_PONCTUATION_INDEX;
	}

	public static int getPosConjunctionsFrequency() {
		return POS_CONJUNCTION_INDEX;
	}

	public static int getPosInterjectionsFrequency() {
		return POS_INTERJECTION_INDEX;
	}

	
}
