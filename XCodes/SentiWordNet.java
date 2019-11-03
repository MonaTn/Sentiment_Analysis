package lexicon;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class SentiWordNet implements Lexicon {
	private String pathToSWN = "Lexicons/SentiWordNet_3.0.0_1.txt";
	private HashMap<String, Double> wordAndScore;

	public SentiWordNet() {

		wordAndScore = new HashMap<String, Double>();
		HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
		try {
			BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
			String line = "";
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
				Double score = Double.parseDouble(data[2])
						- Double.parseDouble(data[3]);
				String[] words = data[4].split(" ");
				for (String w : words) {
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

			}
			// System.out.println(_temp.toString());
			Set<String> temp = _temp.keySet();
			for (Iterator<String> iterator = temp.iterator(); iterator
					.hasNext();) {
				String word = iterator.next();
				Vector<Double> v = _temp.get(word);
				double score = 0.0;
				double sum = 0.0;
				for (int i = 0; i < v.size(); i++)
					score += ((double) 1 / (double) (i + 1)) * v.get(i);
				for (int i = 1; i <= v.size(); i++)
					sum += (double) 1 / (double) i;
				score /= sum;
				wordAndScore.put(word, score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public double extractScore(String word, String pos) {
		if (wordAndScore.containsKey(word + "#" + pos))
			return wordAndScore.get(word + "#" + pos);

//			return roundTwoDecimals(wordAndScore.get(word + "#" + pos));
		else
			return 0.0;
	}
	@Override
	public String taggAdapter(String tagg) {
		if (tagg.equalsIgnoreCase("Adjective"))
			return "a";
		else if ( tagg.equalsIgnoreCase("Adverb"))
			return "r";
		else if ( tagg.equalsIgnoreCase("Verb"))
			return "v";
		else 
			return null;
	}

	private Double roundTwoDecimals(double score) {
		return Double.valueOf(new DecimalFormat("#.##").format(score));
	}

}
