package lexicon;

import java.util.Map;

public interface Lexicon {
	public double getScore(String word, String tag);

	public Map<String, Double> getMap();

}
