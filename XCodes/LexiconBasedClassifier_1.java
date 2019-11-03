package shared;

/**
 * modification 27 March 2013
 * Last modification 10 September 2013
 */

import java.util.List;

import shared.Word;


import Classifier_Negation.NegationPreprocessor;

import lexicon.Lexicon;

public class LexiconBasedClassifier_1 {

	public boolean predictPolarity(double score) {
		return (score > 0) ? true : false;
	}

	public double calculateDocumentTotalScoreFromLexicons(Document document,
			List<Lexicon> lexicons) {
		double totalScoreFromAllLexicons = 0;
		for (Lexicon lexicon : lexicons) {
			totalScoreFromAllLexicons += calculateDocumentScore(document,
					lexicon);
		}
		return totalScoreFromAllLexicons;
	}

	public double calculateDocumentScore(Document document, Lexicon lexicon) {
		WordList wordsList = document.getWordsList();
		double totalScore = 0;
		// System.out.println(wordsList.getWords().size());
		for (Word word : wordsList.getWords()) {
			if (isSentimentalTag(word.getTag())) {
				double score = calculateScore(lexicon, word);
				totalScore += score * (double) word.getCount();
				System.out.println(word + ", "+ tag+", " + word.getCount()+ ", " + score " ==> "+ score * word.getCount());
			}
		}
		System.out.println(document.getName() + " by " + lexicon.toString()
				+ " => " + totalScore);
		return totalScore;
	}

	private boolean isSentimentalTag(String tag) {
		return (tag.equals("Adjective") || tag.equals("Verb"));
	}

	private double calculateScore(Lexicon lexicon, Word word) {
		int coefficient = computeCoefficient(word.getWord());
		String wordExact = getExactWord(word.getWord());
		String tag = tagConverter(word.getTag()); // change
		return lexicon.getScore(wordExact, tag) * coefficient;
	}

	private int computeCoefficient(String word) {
		NegationPreprocessor nProcessor = new NegationPreprocessor();
		if (isNegatedWord(word) && !nProcessor.isNegationCues(word)) {
			return -1;
		} else {
			return 1;
		}
	}

	private boolean isNegatedWord(String word) {
		return word.startsWith("~");
	}

	
	private String getExactWord(String word) {
		if (isNegatedWord(word)) {
			word.replaceFirst("~", "");
		}
		return word;
	}

	private String tagConverter(String tag) {
		if (tag.equalsIgnoreCase("Adjective")) {
			return "a";
		} else if (tag.equalsIgnoreCase("Adverb")) {
			return "r";
		} else if (tag.equalsIgnoreCase("Verb")) {
			return "v";
		} else if (tag.equalsIgnoreCase("Noun")) {
			return "n";
		} else {
			return "";
		}
	}

	
}