public class TaggTypes {

	public static String adaptingTagg(String tagg) {
		if (tagg.contains("JJ"))
			return "ADJECTIVE";
		else if (tagg.contains("VB"))
			return "VERB";
		else if (tagg.contains("RB"))
			return "ADVERB";
		else if (tagg.equals("CC"))
			return "CONJUCTION";
		else if (tagg.equals("IN"))
			return "PREPOSITION";
		else if (tagg.contains("NN"))
			return "NOUN";
		else if (tagg.contains("PR"))
			return "PREPOSITION";
		else if (tagg.equals("UH"))
			return "INTERJECTION";
		else if (isPunctuation(tagg))
			return "PUNCTUATION";
		else
			return null;

	}

	public static boolean isPunctuation(String tagg) {
		return (tagg.equals("#") || tagg.equals("$") || tagg.equals("-LRB-")
				|| tagg.equals("-RRB-") || tagg.equals(",") || tagg.equals(".")
				|| tagg.equals(":") || tagg.equals("\"") || tagg.equals("\""));
	}
}
