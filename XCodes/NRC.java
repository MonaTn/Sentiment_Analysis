package lexicon;

/**
 * Non POS tagged lexicon 
 * Binary lexicon
 * Last change 25 February 2013
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NRC implements Lexicon, Serializable {

	private static final long serialVersionUID = 6808724589886806796L;
	private String lexiconFile = "Lexicons/NRC.txt";
	private Map<String, Integer> wordAndScore;
	private BufferedReader bufferedReader;
	private Pattern finderPolarityPattern;
	private Pattern pattern;
	private String regEx = "\t";

	public NRC() {
		finderPolarityPattern = Pattern.compile("\\w+\tpositive\t\\d");
		wordAndScore = new HashMap<String, Integer>();
		buildLexicon();
	}

	private void buildLexicon() { // **
		try {
			bufferedReader = new BufferedReader(new FileReader(lexiconFile));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				addWordsAndScoreToMap(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordsAndScoreToMap(String line) {
		Matcher matcher = finderPolarityPattern.matcher(line);
		if (matcher.find()) {
			addToMap(line);
		}

	}

	private void addToMap(String line) {
		String word = extractWord(line);
		int score = extractScore(line);
		mapUpdate(word, score, wordAndScore);
	}

	private String extractWord(String line) {
		String[] tokens = tokenizeString(line, regEx);
		String[] words = tokens[0].split("--");
		return words[0];
	}

	
	private boolean isTruePolarity (String polarity, String line) {
		String polarityRegEx = "\\w+\t"+polarity+"\t1";
		finderPolarityPattern = Pattern.compile(polarityRegEx);
		Matcher matcher = finderPolarityPattern.matcher(line);
		return matcher.find();
	}
	private int extractScore(String line) {
		if (isPositive(line)) {
			return 1;
		} else if (isNegative(line)) {
			return -1;
		} else {
			return 0;
		}
	}

	private String[] tokenizeString(String text, String regEx) { // **
		return text.split(regEx);
	}

	private boolean isPositive(String line) {
		String[] tokens = tokenizeString(line, "\t");
		int score = Integer.valueOf(tokens[2]);
		return (score == 1) ? true : false;
	}

	private boolean isNegative(String line) {
		try {
			line = bufferedReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] tokens = tokenizeString(line, "\t");
		int score = Integer.valueOf(tokens[2]);
		return (score == 1) ? true : false;
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
	public String tagConverter(String tagg) {
		return "";
	}
}
// private void addWordAndScoreToMap(String line) throws IOException {
// String[] tokens = tokenizeString(line, "\t");
// addToMap(line, tokens);
// }
