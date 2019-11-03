package lexicon;

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

public class SubjectivityClues implements Lexicon, Serializable {

	private static final long serialVersionUID = -8335803672374171957L;
	private final String lexiconName = "Lexicons/subjclueslen1-HLTEMNLP05.tff";
	private Map<String, Integer> wordAndScore;

	public SubjectivityClues() {
		wordAndScore = new HashMap<String, Integer>();
		buildLexicon();
	}

	private void buildLexicon() { // **
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					lexiconName));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				addWordsAndScoreToMap(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordsAndScoreToMap(String line) {
		String[] tokens = tokenizedText(line, "\\s+");
		if (hasPolarity(tokens)) {
			addToMap(tokens);
		}
	}

	private String[] tokenizedText(String text, String regEx) { // **
		return text.split(regEx);
	}

	private boolean hasPolarity(String[] tokens) {
		String[] tag = tokenizedText(tokens[3], "=");
		return (isVerb(tag[1]) || isAdjective(tag[1]) || isAdverb(tag[1]));
	}

	private boolean isVerb(String tag) {
		return (tag.equals("verb"));
	}

	private boolean isAdjective(String tag) {
		return (tag.equals("adj"));
	}

	private boolean isAdverb(String tag) {
		return (tag.equals("adverb"));
	}

	private void addToMap(String[] tokens) {
		String token = buildToken(tokens);
		int score = extractScore(tokens);
		mapUpdate(token, score, wordAndScore);
	}

	private String buildToken(String[] tokens) {
		String word = extractWord(tokens);
		String tag = returnTag(tokens);
		return (word + "#" + tag);
	}

	private String extractWord(String[] tokens) {
		String[] word = tokenizedText(tokens[2], "=");
		return word[1];
	}

	private String returnTag(String[] tokens) {
		String[] tag = tokenizedText(tokens[3], "=");
		if (isAdjective(tag[1])) {
			return "a";
		} else if (isVerb(tag[1])) {
			return "v";
		} else if (isAdverb(tag[1])) {
			return "r";
		} else {
			return null;
		}
	}

	private int extractScore(String[] tokens) {
		String[] polarity = tokenizedText(tokens[5], "=");
		if (polarity[1].equals("positive")) {
			return 1;
		} else if (polarity[1].equals("negative")) {
			return -1;
		} else {
			return 0;
		}
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
		return (wordAndScore.containsKey(word + "#" + pos)) ? wordAndScore
				.get(word + "#" + pos) : 0;
	}

	@Override
	public String tagConverter(String tagg) {
		if (tagg.equalsIgnoreCase("Adjective"))
			return "a";
		else if (tagg.equalsIgnoreCase("Adverb"))
			return "r";
		else if (tagg.equalsIgnoreCase("Verb"))
			return "v";
		else
			return "";
	}

}
