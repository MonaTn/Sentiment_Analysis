package lexicon;

/** 
 * Non POS tagged lexicon 
 * Binary lexicon
 * Last modification 25 February 2013
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MSOL implements Lexicon, Serializable {

	private static final long serialVersionUID = 5870622848074123802L;
	private String lexiconFile = "Lexicons/MOSL.txt";
	private Map<String, Integer> wordAndScore;
	private String regEx = "\\s+";

	public MSOL() {
		wordAndScore = new HashMap<String, Integer>();
		buildLexicon();
	}

	private void buildLexicon() { // **
		try {
			BufferedReader bufferReader = new BufferedReader(new FileReader(
					lexiconFile));
			String line = "";
			while ((line = bufferReader.readLine()) != null) {
				addWordsAndScoreToMap(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordsAndScoreToMap(String line) {
		String[] tokens = tokenizedText(line, regEx);
		addToMap(tokens);
	}

	private String[] tokenizedText(String text, String regEx) { // **
		return text.split(regEx);
	}

	private void addToMap(String[] tokens) {
		String word = extractWord(tokens);
		int score = extractScore(tokens);
		mapUpdate(word, score, wordAndScore);
	}

	private String extractWord(String[] tokens) {
		String word = tokens[0];
		return word.replaceAll("\\_", " ");
	}

	private int extractScore(String[] tokens) {
		String polarity = tokens[1];
		return (polarity.equals("positive")) ? 1 : -1;
	}

	private void mapUpdate(String word, int score, Map<String, Integer> map) {
		map.put(word, score);
	}

	@Override
	public double extractScore(String word, String pos) {
		return (double) ((wordAndScore.containsKey(word)) ? wordAndScore
				.get(word) : 0);
	}

	@Override
	public String tagConverter(String tagg) {
		return "";
	}

}