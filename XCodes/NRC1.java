package lexicon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NRC implements Lexicon, Serializable {

	private static final long serialVersionUID = -7509266881438409047L;
	private String nrc = "Lexicons/NRC.txt";
	private Map<String, Integer> wordAndScore;

	public NRC() {
		wordAndScore = new HashMap<String, Integer>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					nrc));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				addWordsAndScoreToMap(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordsAndScoreToMap(String line) {
		String[] tokens = line.split("\t");
		addToArraylist(tokens);
		addToMap(tokens);
	}

	private void addToArraylist(String[] tokens) {
		
	}
	private void addToMap(String[] tokens) {
		String token = buildToken(tokens);
		int score = extractScore(tokens);
		mapUpdate(token, score, wordAndScore);
	}

	private String buildToken(String[] tokens) {
		return  extractWord(tokens);
	}

	private String extractWord(String[] tokens) {
		String[] word = tokens[0].split("--");
		return word[0];
	}

	private int extractScore(String[] tokens) {
		String[] polarity = tokens[5].split("=");
		if (polarity[1].equals("positive")) {
			return 1;
		} else if (polarity[1].equals("negative")) {
			return -1;
		} else {
			return 0;
		}
	}

	private void mapUpdate(String token, int score,
			Map<String, Integer> wordAndScore) {
		int finalScore = calculateScore(token, score, wordAndScore);
		wordAndScore.put(token, finalScore);
	}

	private int calculateScore(String token, int score,
			Map<String, Integer> wordAndScore) {
		if (wordAndScore.containsKey(token)) {
			int value = wordAndScore.get(token);
			return assignScore(score, value);
		}
		return score;
	}

	private int assignScore(int score, int value) {
		if (value == 0 || value == score) {
			return score;
		} else {
			return value;
		}
	}

	@Override
	public double extractScore(String word, String pos) {
		return (wordAndScore.containsKey(word + "#" + pos)) ? wordAndScore
				.get(word + "#" + pos) : 0;
	}

	@Override
	public String tagConverter(String tagg) {
		return "";
	}

}
