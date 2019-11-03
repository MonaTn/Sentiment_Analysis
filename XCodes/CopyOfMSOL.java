package lexicon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CopyOfMSOL implements Lexicon, Serializable {

	private static final long serialVersionUID = 5870622848074123802L;
	private String lexiconFile = "Lexicons/MOSL_.txt";
	private Map<String, Integer> wordAndScore;

	public CopyOfMSOL() {
		wordAndScore = new HashMap<String, Integer>();
		buildLexicon();
	}

	private void buildLexicon() {
		try {
			BufferedReader bufferReaderOfLexicon = new BufferedReader(
					new FileReader(lexiconFile));
			String line = "";
			while ((line = bufferReaderOfLexicon.readLine()) != null) {
				addWordsAndScoreToMap(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordsAndScoreToMap(String line) {
		String[] tokens = line.split("\\s+");
		int score = (tokens[1].equals("positive")) ? 1 : -1;
		wordAndScore.put(tokens[0], score);
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