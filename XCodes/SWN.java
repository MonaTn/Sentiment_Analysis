package xTestUtil;

/** 
 * POS tagged Lexicon
 * Continues lexicon
 * Last modification 25 February 2013
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


public class SWN implements LexicInterface, Serializable {

	private static final long serialVersionUID = 4032049344062628541L;
	private String swn = "Lexicons/SentiWordNet_3.0.0_1.txt";
	private HashMap<String, Double> wordAndScore;

	public SWN() {
		wordAndScore = new HashMap<String, Double>();
		buildLexicon();
	}

	private void buildLexicon() {
		HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					swn));
			addWordsAndAllRelatedScoresToTempMap(_temp, bufferedReader);
			addWordAlongeSingleScoreToMap(_temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addWordAlongeSingleScoreToMap(
			HashMap<String, Vector<Double>> _temp) throws IOException {
		Set<String> temp = _temp.keySet();
		for (Iterator<String> iterator = temp.iterator(); iterator.hasNext();) {
			String word = iterator.next();
			Vector<Double> scoreVectore = _temp.get(word);
			double score = 0.0;
			double sum = 0.0;
			for (int i = 0; i < scoreVectore.size(); i++)
				score += ((double) 1 / (double) (i + 1)) * scoreVectore.get(i);
			for (int i = 1; i <= scoreVectore.size(); i++)
				sum += (double) 1 / (double) i;
			score /= sum;
			wordAndScore.put(word, score);
		}
	}

	private void addWordsAndAllRelatedScoresToTempMap(
			HashMap<String, Vector<Double>> _temp, BufferedReader bufferedReader)
			throws IOException {
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			String[] data = tokenizedText(line, "\t");
			Double score = Double.parseDouble(data[2])
					- Double.parseDouble(data[3]);
			String[] words = data[4].split(" ");
			for (String w : words) {
				exteractWordAndScore(_temp, data, score, w);
			}
		}
	}

	private String[] tokenizedText(String text, String regEx) { // **
		return text.split(regEx);
	}

	private void exteractWordAndScore(HashMap<String, Vector<Double>> _temp,
			String[] data, Double score, String w) {
		String[] w_n = w.split("#");
		w_n[0] += "#" + data[0];
		int index = Integer.parseInt(w_n[1]) - 1;
		if (_temp.containsKey(w_n[0])) {
			Vector<Double> v = _temp.get(w_n[0]);
			if (index > v.size())
				for (int i = v.size(); i < index; i++)
					v.add(0.0);
			v.add(index, score);
			_temp.put(w_n[0], v);
		} else {
			Vector<Double> v = new Vector<Double>();
			for (int i = 0; i < index; i++)
				v.add(0.0);
			v.add(index, score);
			_temp.put(w_n[0], v);
		}
	}

	@Override
	public double extractScore(String word, String pos) {
		return (wordAndScore.containsKey(word + "#" + pos)) ? wordAndScore
				.get(word + "#" + pos) : 0;
	}



}
