package lexicon;

/**
 * Last modification 1 April 2013
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractLexicon implements Serializable, Lexicon {

	private static final long serialVersionUID = -7409448057673245448L;
	protected String regEx = "\t";
	protected Map<String, Double> wordAndScore;
	protected boolean posedLexicon;

	public double getScore(String word, String tag) {
		String token = (posedLexicon == true) ? word + "#" + tag : word;
		return (wordAndScore.containsKey(token)) ? wordAndScore.get(token) : 0;
	}

	public Map<String, Double> getMap() {
		return wordAndScore;
	}

	protected void buildLexicon(String lexiconFile) {
		wordAndScore = new HashMap<String, Double>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					lexiconFile));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				addWordsAndScoreToMap(line);
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void addWordsAndScoreToMap(String line) {
		String[] tokens = line.split(regEx);
		String token = buildToken(tokens);
		if (token != null) {
			double initialScore = extractScore(tokens);
			double score = computeScore(token, initialScore);
			wordAndScore.put(token, score);
		}
	}

	protected double computeScore(String token, double score) {
		if (wordAndScore.containsKey(token)) {
			return (wordAndScore.get(token) == 0) ? score : wordAndScore
					.get(token);
		} else {
			return score;
		}
	}

	protected abstract String buildToken(String[] tokens);

	protected abstract double extractScore(String[] tokens);

}
