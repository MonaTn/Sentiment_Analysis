package negation;

import java.util.Arrays;

public class NegationCues {
	private static String[] cues = { "no", "not", "n't", "n’t", "nope",
			"never", "barely", "scarcely", "hardly", "nothing", "neither",
			"nor", "nobody", "none", "nowhere", "without", "lack", "lacked",
			"lacking", "lacks" };

	public static boolean isCues(String word) {
		return Arrays.asList(cues).contains(word.toLowerCase());
	}

}