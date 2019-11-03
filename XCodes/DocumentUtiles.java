import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class DocumentUtiles {

	private static PorterStemmer stemmer = new PorterStemmer();
	private static PrintWriter printWriter;

	public static boolean predictPolarity(Document document, Lexicon lexicon, MaxentTagger taggerModel) throws ClassNotFoundException,
			IOException {
		Map<String, WordProperties> opinionatedWords = extractVerbAndAdjective(
				document, taggerModel);
		double score = computeTotalScoreOfOpinionatedWords(opinionatedWords,
				lexicon);
		printlnOpinionatedMap(opinionatedWords);
		printWriter = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"), true);
		printWriter.println("Lexicon is " + lexicon.toString() + "   Score is : "
				+ score);
		return (score > 0) ? true : false;
	}// +
	
	public static CaseDescription computeCaseDescription(Document document,
			MaxentTagger taggerModel) throws ClassNotFoundException,
			IOException {
		Map<String, Integer> allTaggs = new HashMap<String, Integer>();
		Map<String, Integer> allWords = new HashMap<String, Integer>();

		int totalSentences = 0;

		for (List<HasWord> sentence : document.getDocumentPreprocessor()) {
			List<TaggedWord> tagSentence = taggerModel.tagSentence(sentence);
			// pw.println (Sentence.listToString(tagSentence, false));
			totalSentences++;
			addWordsToWordsMap(tagSentence, allWords);
			addTaggsToTaggsMap(tagSentence, allTaggs);
		}
		CaseDescription caseDescription = new CaseDescription();
		caseDescription.setFeatures(allWords, allTaggs, totalSentences);
		// pw.println(allWords.toString());
		// pw.println(allTaggs.toString());
		// pw.println (Arrays.toString(caseDescription.getFeatures()));
		return caseDescription;
	}// ++++

	// think about as a public method for both test & train 
	public static Map<String, WordProperties> extractVerbAndAdjective(
			Document document, MaxentTagger taggerModel)
			throws ClassNotFoundException, IOException {
		Map<String, WordProperties> opinionatedWords = new HashMap<String,WordProperties>();
		for (List<HasWord> sentence : document.getDocumentPreprocessor()) {
			List<TaggedWord> tagSentence = taggerModel.tagSentence(sentence);
			for (TaggedWord taggedWord : tagSentence) {
				if (isSentimentalTagg(taggedWord.tag())) {
					String word = (isVerbNeedToStem(taggedWord.tag())) ? stemmer.stem(taggedWord.word()) : taggedWord.word();
					int count = (opinionatedWords.containsKey(word)) ? opinionatedWords
							.get(word).getCount() + 1 : 1;
					opinionatedWords.put(word, new WordProperties(getPOSName(taggedWord.tag()),count,(double) 0));
				}
			}
		}
		return opinionatedWords;
	}
	
	public static double computeTotalScoreOfOpinionatedWords(
			Map<String, WordProperties> opinionatedWords, Lexicon lexicon)
			throws UnsupportedEncodingException {
		getScoresOfEachOpinionatedWord(opinionatedWords, lexicon);
		double totalScore = 0;
		for (WordProperties valuePair : opinionatedWords.values()) {
			totalScore += valuePair.getScore() * (double) valuePair.getCount();
		}
		return totalScore;
	}

	private static void getScoresOfEachOpinionatedWord(
			Map<String,WordProperties> opinionatedWords, Lexicon lexicon)
			throws UnsupportedEncodingException {
		for (Map.Entry<String, WordProperties> entry : opinionatedWords
				.entrySet()) {
			String tagg = lexicon.taggAdapter(entry.getValue().getTagg());
			double score = lexicon.extractScore(entry.getKey(), tagg);
			entry.setValue(new WordProperties(tagg, entry.getValue().getCount(), score));
		}
	}
	
	private static boolean isSentimentalTagg(String posTagg) {
		return (posTagg.contains("VB") || posTagg.contains("JJ"));
	}
	

	private static void addWordsToWordsMap(List<TaggedWord> tagSentence,
			Map<String, Integer> words) {
		for (TaggedWord taggedWord : tagSentence) {
			String word = (isVerbNeedToStem(taggedWord.tag())) ? stemmer
					.stem(taggedWord.word()) : taggedWord.word();
			// pw.println(taggedWord.word()+"   "+taggedWord.tag()+ " = "
			// +extracTaggType(taggedWord.tag()));
			int count = (words.containsKey(word)) ? words.get(word) + 1 : 1;
			words.put(word, count);
		}
	}

	private static void addTaggsToTaggsMap(List<TaggedWord> tagSentence,
			Map<String, Integer> taggs) {
		for (TaggedWord taggedWord : tagSentence) {
			String tagg = getPOSName(taggedWord.tag());
			int count = (taggs.containsKey(tagg)) ? taggs.get(tagg) + 1 : 1;
			taggs.put(tagg, count);
		}
	}

	private static boolean isVerbNeedToStem(String tagg) {
		return (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg
				.equals("VBZ"));
	}

	private static String getPOSName(String tagg) {
		String taggType = (tagg.matches("[\\$,\\.:#]") || tagg.endsWith("RB-")
				|| tagg.equals("\''") || tagg.matches("\\``") || tagg
				.equals("\"")) ? "PUNCT" : tagg;
		return POSType.valueOf(taggType).getName();
	}
	
	 private static void printlnOpinionatedMap(
			 Map<String, WordProperties> OpinionatedWordMap)
			 throws UnsupportedEncodingException {
			 PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,
			 "utf-8"), true);
			
			 for (Map.Entry<String, WordProperties> entry : OpinionatedWordMap
			 .entrySet())
			 pw.printf("\n Word : %25s  count : %5d Score = %3.2f ", entry
			 .getKey()+"#"+ entry.getValue().getTagg(),entry.getValue().getCount(), entry.getValue()
			 .getScore());
			
			 }
}