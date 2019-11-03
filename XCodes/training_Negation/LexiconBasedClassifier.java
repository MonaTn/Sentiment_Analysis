package training_Negation;

/**
 * modification 27 March 2013
 * modification 10 September 2013
 * modification 16 October 2013
 * Last modification 28 November 2013
 * Dec 9 : change equalIgnoreCase ==> startsWith in isSentimentalTag and tagConverter
 */

import java.util.List;

import lexicon.Lexicon;
import shared.Document;
import shared.Word;
import shared.WordList;
import Negation.NegationCues;

public class LexiconBasedClassifier {

	public boolean predictPolarity(double score) {
		return (score > 0) ? true : false;
	}

	public double calculateTotalDocumentScoreByAllLexicons(Document document,
			List<Lexicon> lexicons) {
		double totalScoreByAllLexicons = 0;
		for (Lexicon lexicon : lexicons) {
			totalScoreByAllLexicons += getDocumentScore(document, lexicon);
		}
		return totalScoreByAllLexicons;
	}

	public double getDocumentScore(Document document, Lexicon lexicon) {
		WordList wordsList = document.getWordsList();
		double totalScore = 0;
		for (Word word : wordsList.getWords()) {
			if (isSentimentalTag(word.getTag())) {
				double score = getWordScore(lexicon, word);
				totalScore += score * word.getCount();
			}
		}
		// System.out.println(document.getName() + " by " + lexicon.toString()
		// + " => " + totalScore);
		return totalScore;
	}

	private boolean isSentimentalTag(String tag) { // Dec 9
		return (tag.startsWith("Adjective") || tag.startsWith("Verb"));
	}

	private double getWordScore(Lexicon lexicon, Word word) {
		int coefficient = getWordCoefficient(word.getWord());
		String absoluteWord = getAbsoluteWord(word.getWord());
		String tag = tagConverter(word.getTag()); // change
		return lexicon.getScore(absoluteWord, tag) * coefficient;
	}

	private int getWordCoefficient(String word) {
		if (isNegatedWord(word) && !NegationCues.isCues(word)) {
			return -1;
		} else {
			return 1;
		}
	}

	private boolean isNegatedWord(String word) {
		return word.startsWith("~");
	}

	private String getAbsoluteWord(String word) {
		if (isNegatedWord(word)) {
			word.replaceFirst("~", "");
		}
		return word;
	}

	private String tagConverter(String tag) {
		if (tag.startsWith("Adjective")) { // Dec 9
			return "a";
		} else if (tag.startsWith("Adverb")) {
			return "r";
		} else if (tag.startsWith("Verb")) {
			return "v";
		} else if (tag.equalsIgnoreCase("Noun")) {
			return "n";
		} else {
			return "";
		}
	}

}