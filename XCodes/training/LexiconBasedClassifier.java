package training;

/**
 * modification 27 March 2013
 * Dec 9 : change equalIgnoreCase ==> startsWith in isSentimentalTag and tagConverter
 */

import java.util.List;

import shared.Document;
import shared.Word;
import shared.WordList;

import lexicon.Lexicon;

public class LexiconBasedClassifier {

	public boolean predictPolarity(double score) {
		return (score > 0) ? true : false;
	}

	public double calculateTotalDocumentScoreByAllLexicons(Document document,
			List<Lexicon> lexicons) {
		double totalScoreByAllLexicons = 0;
		for (Lexicon lexicon : lexicons) {
			totalScoreByAllLexicons += calculateDocumentScore(document, lexicon);
		}
		return totalScoreByAllLexicons;
	}

	public double calculateDocumentScore(Document document, Lexicon lexicon) {
//		 System.out.println("\n"+lexicon.getClass().getName());
		WordList wordsList = document.getWordsList();
		double totalScore = 0;
		for (Word word : wordsList.getWords()) {
			if (isSentimentalTag(word.getTag())) {
				String tag = tagConverter(word.getTag()); // change
				double score = lexicon.getScore(word.getWord(), tag); // dec 9
//				 System.out.println(word.getWord()+", "+tag+", "+word.getCount()+" , Score : "+score+" ==> "+ word.getCount()*score);
				totalScore += score * (double) word.getCount();
			}
		}
//		 System.out.println(document.getName() + " by " + lexicon.toString()
//		 + " => " + totalScore);
		return totalScore;
	}

	private boolean isSentimentalTag(String tag) {
		return (tag.startsWith("Adjective") || tag.startsWith("Verb"));
	}

	private String tagConverter(String tag) {
		if (tag.startsWith("Adjective")) { // Dce 9
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
