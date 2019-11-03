import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CaseBasePopulating {

	public static List<Class<? extends Lexicon>> findTrueLexicon(
			Map<String, Pair<Integer, Double>> opinionatedWords,
			boolean truePolarity,
			List<Class<? extends Lexicon>> sentimentLexicons) throws Exception,
			Exception {
		List<Class<? extends Lexicon>> solutionSet = new ArrayList<Class<? extends Lexicon>>();
		for (Class<? extends Lexicon> sentimentalLexicon : sentimentLexicons) {
			Lexicon lexicon = sentimentalLexicon.newInstance();
			setScoresOfOpinionatedWords(opinionatedWords, lexicon);
//			printlnOpinionatedMap(opinionatedWords);
			if (obtainedPolarity(opinionatedWords) == truePolarity)
				solutionSet.add(sentimentalLexicon);
		}
		return solutionSet;
	}

	private static void setScoresOfOpinionatedWords(
			Map<String, Pair<Integer, Double>> opinionatedWords, Lexicon lexicon)
			throws UnsupportedEncodingException {
		for (Map.Entry<String, Pair<Integer, Double>> entry : opinionatedWords
				.entrySet()) {
			int indexOfSeperator = entry.getKey().indexOf("#");
			String word = entry.getKey().substring(0, indexOfSeperator);
			String tagg = entry.getKey().substring(indexOfSeperator + 1,
					entry.getKey().length());
			tagg = lexicon.taggAdapter(tagg);
			double score = lexicon.extractScore(word, tagg);
			entry.setValue(new Pair<Integer, Double>(entry.getValue()
					.getCount(), score));
		}
	}

	private static boolean obtainedPolarity(
			Map<String, Pair<Integer, Double>> opinionatedWords) {
		double totalScore = (double) 0;
		for (Pair<Integer, Double> valuePair : opinionatedWords.values())
			totalScore += valuePair.getScore() * (double) valuePair.getCount();
		System.out.println("\n Totalscore = " + totalScore);
		return (totalScore > 0) ? true : false;
	}

//	private static void printlnOpinionatedMap (Map<String, Pair<Integer, Double>> OpinionatedWordMap ) throws UnsupportedEncodingException {
//	PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,"utf-8"), true);
//
//	for (Map.Entry<String, Pair<Integer, Double>> entry : OpinionatedWordMap.entrySet())
//		pw.printf("\n Word : %25s  count : %5d Score = %3.2f ", entry.getKey(), entry.getValue().getCount(), entry.getValue().getScore()) ;
//	
//	}
	
}
