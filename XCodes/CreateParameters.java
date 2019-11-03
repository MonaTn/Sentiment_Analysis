import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.TaggedWord;


public class CreateParameters {

	public static void addToLexiconsSet (List<Class<? extends Lexicon>> sentimentLexicons) {
		sentimentLexicons.add(SentiWordNet.class);
		sentimentLexicons.add(MSOL.class);
	}

	public static void addToOpinionatedMap (List<TaggedWord> tSentence, Map <String , Pair<Integer, Double>> opinionatedWords) {
		for (TaggedWord taggedWord : tSentence) {
			if (isValidTagg(taggedWord.tag())) {
				String key = extractStem(taggedWord)+"#"+TaggTypes.adaptingTagg(taggedWord.tag()) ;
				int count = (opinionatedWords.containsKey(key)) ? opinionatedWords.get(key).getCount() + 1 : 1;
				Pair<Integer, Double> pair = new Pair<Integer, Double>(count, (double) 0);
				opinionatedWords.put(key, pair);
			}
		}
	}
	
	private static boolean isValidTagg (String posTagg) {
		return (posTagg.contains("VB") || posTagg.contains("JJ") );//|| posTagg.equals("RB")|| posTagg.equals("RBR") || posTagg.equals("RBS")) ; 
	}

	private static String extractStem (TaggedWord taggedWord) {
		return  (taggedWord.tag().equals("VB") || taggedWord.tag().equals("VBP") || taggedWord.tag().contains("JJ")) ? taggedWord.word() : new PorterStemmer().stem(taggedWord.word());
	}
	
	public static void addToTaggsMap (List<TaggedWord> tSentence , Map <String , Integer> taggs) {
		for (TaggedWord taggedWord : tSentence) {
			String taggKey = TaggTypes.adaptingTagg(taggedWord.tag());
			int count = (taggs.containsKey(taggKey)) ? taggs.get(taggKey) + 1 : 1;
			taggs.put(taggKey, count);
		}
	}
	

	public static void addToWordsMap (List<TaggedWord> tSentence , Map <String , Integer> words) {
		for (TaggedWord taggedWord : tSentence) {
			String key = createKeyWord(taggedWord.tag(), taggedWord.word());
			int count = (words.containsKey(key)) ? words.get(key) + 1 : 1;
			words.put(key, count);
		}
	}

	private static String createKeyWord (String tagg , String word) {
		return  (isVerbNeedToStem(tagg)) ? new PorterStemmer().stem(word) : word;
	}

	private static boolean isVerbNeedToStem (String tagg) {
		return  (tagg.equals("VBD") || tagg.equals("VBG") || tagg.endsWith("VBN") || tagg.endsWith("VBZ")); 
	}
}
