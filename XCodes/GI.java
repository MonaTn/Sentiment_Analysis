package xTestUtil;

/** 
 * POS tagged Lexicon
 * Binary lexicon
 * Last modification 25 February 2013
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class GI implements LexicInterface, Serializable {

	private static final long serialVersionUID = -8727298228525036877L;
	private String lexiconFile = "Lexicons/inquirerbasicttabsclean";
	private Map<String, Integer> wordAndScore;
	private String regEx = "\t";

	public GI() {
		wordAndScore = new HashMap<String, Integer>();
		buildLexicon();
	}

	private void buildLexicon() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					lexiconFile));
			String line = bufferedReader.readLine(); // read header
			while ((line = bufferedReader.readLine()) != null) {
				addWordsAndScoreToMap(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordsAndScoreToMap(String line) {
		String[] tokens = tokenizedText(line, regEx);
		if (hasPolarity(tokens)) {
			addToMap(tokens);
		}
	}

	private String[] tokenizedText(String text, String regEx) { // **
		return text.split(regEx);
	}

	private boolean hasPolarity(String[] tokens) {
		return (isVerb(tokens) || isAdjective(tokens) || isAdverb(tokens));
	}

	private boolean isVerb(String[] tokens) {
		return (tokens[116].equals("IAV") || tokens[117].equals("DAV") || tokens[118]
				.equals("SV"));
	}

	private boolean isAdjective(String[] tokens) {
		return (tokens[119].equals("IPadj") || tokens[120].equals("IndAdj"));
	}

	private boolean isAdverb(String[] tokens) {
		return (tokens[184].equals("DEG"));
	}

	private void addToMap(String[] tokens) {
		String token = buildToken(tokens);
		int score = extractScore(tokens);
		updateMap(token, score, wordAndScore);
	}

	private String buildToken(String[] tokens) {
		String word = extractWord(tokens);
		String tag = returnTag(tokens);
		return (word + "#" + tag).toLowerCase();
	}

	private String extractWord(String[] tokens) {
		String[] word = tokens[0].split("#");
		return word[0];
	}

	private String returnTag(String[] tokens) { // **
		if (isAdjective(tokens)) {
			return "a";
		} else if (isVerb(tokens)) {
			return "v";
		} else if (isAdverb(tokens)) {
			return "r";
		} else
			return null;
	}

	private int extractScore(String[] tokens) {
		if (tokens[2].equals("Positiv")) {
			return 1;
		} else if (tokens[3].equals("Negativ")) {
			return -1;
		} else
			return 0;
	}

	private void updateMap(String token, int score, Map<String, Integer> map) { // **
		int newScore = computeScore(token, score, map);
		map.put(token, newScore);
	}

	private int computeScore(String token, int score, Map<String, Integer> map) { // **
		if (map.containsKey(token)) {
			return (map.get(token) == 0) ? score : map.get(token);
		} else {
			return score;
		}
	}

	@Override
	public double extractScore(String word, String pos) {
		return (wordAndScore.containsKey(word + "#" + pos)) ? wordAndScore
				.get(word + "#" + pos) : 0;
	}

	
}
