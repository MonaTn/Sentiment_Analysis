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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NRC implements Lexicon, Serializable {

	private static final long serialVersionUID = 6808724589886806796L;
	private String lexiconFile = "Lexicons/NRC.txt";
	private Map<String, Integer> wordAndScore;

	public NRC() {
		wordAndScore = new HashMap<String, Integer>();
		buildLexicon();
	}

	private void buildLexicon() { // **
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					lexiconFile));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				addWordsAndScoreToMap(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordsAndScoreToMap(String line) {
		if (hasPolarity(line)) {
			addToMap(line);
		}
	}

	private boolean hasPolarity(String line) {
		return (isPositive(line) || isNegative(line) || isNeutral(line));
	}

	private boolean isPositive(String line) {
		return checkPattern(line, "positive", 1);
	}

	private boolean isNegative(String line) {
		return checkPattern(line, "negative", 1);
	}

	private boolean isNeutral(String line) {
		return (checkPattern(line, "positive", 0) || checkPattern(line,
				"negative", 0));
	}

	private boolean checkPattern(String line, String word, int digit) {
		String patternRegEx = "\\w+\t" + word + "\t" + digit;
		Pattern patternToFind = Pattern.compile(patternRegEx);
		Matcher matcher = patternToFind.matcher(line);
		return matcher.find();
	}

	private void addToMap(String line) {
		String word = extractWord(line);
		int score = extractScore(line);
		mapUpdate(word, score, wordAndScore);
	}

	private String extractWord(String line) {
		String[] tokens = tokenizeString(line, "\t");
		String[] words = tokenizeString(tokens[0], "--");
		return words[0];
	}

	private int extractScore(String line) {
		if (isPositive(line)) {
			return 1;
		} else if (isNegative(line)) {
			return -1;
		} else { // is neutral
			return 0;
		}
	}

	private String[] tokenizeString(String text, String regEx) { // **
		return text.split(regEx);
	}

	private void mapUpdate(String token, int score, Map<String, Integer> map) {
		int newScore = computeScore(token, score, map);
		map.put(token, newScore);
	}

	private int computeScore(String token, int score, Map<String, Integer> map) {
		if (map.containsKey(token)) {
			return (map.get(token) == 0) ? score : map.get(token);
		} else {
			return score;
		}
	}

	@Override
	public double extractScore(String word, String pos) {
		return (double) ((wordAndScore.containsKey(word)) ? wordAndScore
				.get(word) : 0);
	}

	@Override
	public String tagConverter(String tag) {
		return "";
	}
}
