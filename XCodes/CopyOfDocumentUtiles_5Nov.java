package shared;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lexicon.Lexicon;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class CopyOfDocumentUtiles_5Nov {

	private static PorterStemmer stemmer = new PorterStemmer();

	public static boolean predictPolarity(Document document, Lexicon lexicon) throws ClassNotFoundException,
			IOException {
		double score = computeTotalScoreOfOpinionatedWords(document.getWordsMap(),lexicon);
		System.out.printf("\n Document : %20s By Lexicon : %20s score = %f", document.getName(), lexicon.getClass().getName(), score);
		return (score > 0) ? true : false;
	}
	
	public static void extractTokensAndSentences(Document document, MaxentTagger taggerModel)
			throws ClassNotFoundException, IOException {
		int totalSentencesNumbers = 0;
		Map<String, WordProperties> wordsMap = new HashMap<String,WordProperties>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessing( document);
		for (List<HasWord> sentence : documentPreprocessor) {
			totalSentencesNumbers++;
			List<TaggedWord> tagSentence = taggerModel.tagSentence(sentence);
			for (TaggedWord taggedWord : tagSentence) {
				String word = (isVerbNeedToStem(taggedWord.tag())) ? stemmer
						.stem(taggedWord.word()) : taggedWord.word(); //TODO is it necessary do stemming for Names too ? 
				String tagg = getPOSName(taggedWord.tag());
				int count = (wordsMap.containsKey(word)) ? wordsMap.get(word).getCount() + 1 : 1;
				wordsMap.put(word, new WordProperties(tagg, count, 0));
			}
		}
		document.setWordsMap(wordsMap);
		document.setTotalSentencesNumbers(totalSentencesNumbers);
	}
	public static double computeTotalScoreOfOpinionatedWords(Map<String,WordProperties> wordsMap, Lexicon lexicon)
			throws UnsupportedEncodingException {
		double totalScore = 0;
		for (Map.Entry<String, WordProperties> entry : wordsMap.entrySet()) {
			if (entry.getValue().getTagg().equalsIgnoreCase("Adjective") || entry.getValue().getTagg().equalsIgnoreCase("Verb")) {
				String tagg = lexicon.taggAdapter(entry.getValue().getTagg());
				double score = lexicon.extractScore(entry.getKey(), tagg);
				totalScore += score * (double) entry.getValue().getCount();
//				entry.setValue(new WordProperties(entry.getValue().getTagg(), entry.getValue().getCount(), score));
			}
		}
		return totalScore;
	}
//	public static double computeTotalScoreOfOpinionatedWords(
//			Map<String, WordProperties> wordsMap, Lexicon lexicon)
//			throws UnsupportedEncodingException {
//		getScoresOfEachOpinionatedWord(wordsMap, lexicon);
//		double totalScore = 0;
//		for (WordProperties wordProperties : wordsMap.values()) { // use variable "tagg" or this form is enough readable?
//			if (wordProperties.getTagg().equalsIgnoreCase("Adjective") || wordProperties.getTagg().equalsIgnoreCase("Verb")) {
//				totalScore += wordProperties.getScore() * (double) wordProperties.getCount();
//			}
//		}
//		return totalScore;
//	}
//
//	private static void getScoresOfEachOpinionatedWord(
//			Map<String,WordProperties> wordsMap, Lexicon lexicon)
//			throws UnsupportedEncodingException {
//		for (Map.Entry<String, WordProperties> entry : wordsMap
//				.entrySet()) {
//			if (entry.getValue().getTagg().equalsIgnoreCase("Adjective") || entry.getValue().getTagg().equalsIgnoreCase("Verb")) {
//				String tagg = lexicon.taggAdapter(entry.getValue().getTagg());
//				double score = lexicon.extractScore(entry.getKey(), tagg);
//				entry.setValue(new WordProperties(entry.getValue().getTagg(), entry.getValue().getCount(), score));
//			}
//		}
//	}
	
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
	
	private static DocumentPreprocessor documentPreprocessing (Document document)
				throws ClassNotFoundException, IOException {
			TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
					new CoreLabelTokenFactory(), "untokenizable=noneKeep");
			String absoluteDocName = document.getPath() + "/" + document.getName();
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(absoluteDocName), "utf-8"));
			DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
					bufferReader);
			documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
			
			return documentPreprocessor;
		}
	 
		public static void computeCaseDescription(Document document) throws ClassNotFoundException,
				IOException {
			CaseDescription caseDescription = new CaseDescription();
			caseDescription.setFeatures(document.getWordsMap(),document.getTotalSentencesNumbers());
			document.setCaseDescription(caseDescription);
		}
}
