import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PopulationTheCaseBase {

	private static PorterStemmer stemmer = new PorterStemmer();;
	private static PrintWriter pw;

	public static List<CaseBase> buildCaseBaseSet(
			List<Document> trainingDocumentsSet,
			List<Class<? extends Lexicon>> sentimentLexicons,
			MaxentTagger taggerModel , boolean truePolarityOfTrainingSet) throws Exception, IOException {

		pw = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"), true);

		List<CaseBase> caseBaseSet = new ArrayList<CaseBase>();

		int discardedDocument = 0;

		for (Document document : trainingDocumentsSet) {
			List<Class<? extends Lexicon>> caseSolution = new ArrayList<Class<? extends Lexicon>>();
			for (Class<? extends Lexicon> sentimentalLexicon : sentimentLexicons) {
				Lexicon lexicon = sentimentalLexicon.newInstance();
				boolean obtainedPolarity = predictPolarity(document, lexicon,
						taggerModel);
				if (obtainedPolarity == truePolarityOfTrainingSet) {
					caseSolution.add(sentimentalLexicon);
				} else {
					continue;
				}
			}
			if (!caseSolution.isEmpty()) {
				CaseDescription caseDescription = computeCaseDescription(
						document, taggerModel);
				caseBaseSet.add(new CaseBase(document.getName(),
						caseDescription, caseSolution));
			} else {
				discardedDocument++;
			}
		}
		pw.printf("\n Number of discarded document is %d ", discardedDocument);
		return caseBaseSet;
	}

	// ********************************************************************************************

	private static boolean predictPolarity(Document document, Lexicon lexicon,
			MaxentTagger taggerModel) throws ClassNotFoundException,
			IOException {
		Map<String, Pair<Integer, Double>> opinionatedWords = extractVerbAndAdjective(
				document, taggerModel);
		double score = computeTotalScoreOfOpinionatedWords(opinionatedWords,
				lexicon);
		 printlnOpinionatedMap(opinionatedWords);
		pw.println("Lexicon is " + lexicon.toString() + "   Score is : "
				+ score);
		return (score > 0) ? true : false;
	}// +
	
	private static boolean isSentimentalTagg(String posTagg) {
		return (posTagg.contains("VB") || posTagg.contains("JJ"));
	}
	
	private static Map<String, Pair<Integer, Double>> extractVerbAndAdjective(
			Document document, MaxentTagger taggerModel)
			throws ClassNotFoundException, IOException {
		Map<String, Pair<Integer, Double>> opinionatedWords = new HashMap<String, Pair<Integer, Double>>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document); // ??????
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> tagSentence = taggerModel.tagSentence(sentence);
			for (TaggedWord taggedWord : tagSentence) {
				if (isSentimentalTagg(taggedWord.tag())) {
					String word = (isVerbNeedToStem(taggedWord.tag())) ? stemmer
							.stem(taggedWord.word()) : taggedWord.word();
					word += "#" + extracTaggType(taggedWord.tag());
					int count = (opinionatedWords.containsKey(word)) ? opinionatedWords
							.get(word).getCount() + 1 : 1;
					opinionatedWords.put(word, new Pair<Integer, Double>(count,
							(double) 0));
				}
			}
		}
		return opinionatedWords;
	}
	
	private static double computeTotalScoreOfOpinionatedWords(
			Map<String, Pair<Integer, Double>> opinionatedWords, Lexicon lexicon)
			throws UnsupportedEncodingException {
		setScoresOfEachOpinionatedWord(opinionatedWords, lexicon);
		double totalScore = (double) 0;
		for (Pair<Integer, Double> valuePair : opinionatedWords.values()) {
			totalScore += valuePair.getScore() * (double) valuePair.getCount();
		}
		return totalScore;
	}

	private static void setScoresOfEachOpinionatedWord(
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

	private static CaseDescription computeCaseDescription(Document document,
			MaxentTagger taggerModel) throws ClassNotFoundException,
			IOException {
		Map<String, Integer> allTaggs = new HashMap<String, Integer>();
		Map<String, Integer> allWords = new HashMap<String, Integer>();

		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document);
		int totalSentences = 0;

		for (List<HasWord> sentence : documentPreprocessor) {
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
			String tagg = extracTaggType(taggedWord.tag());
			int count = (taggs.containsKey(tagg)) ? taggs.get(tagg) + 1 : 1;
			taggs.put(tagg, count);
		}
	}

	private static DocumentPreprocessor documentPreprocessing(Document document)
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

	private static boolean isVerbNeedToStem(String tagg) {
		return (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg
				.equals("VBZ"));
	}

	private static String extracTaggType(String tagg) {
		String taggType = (tagg.matches("[\\$,\\.:#]") || tagg.endsWith("RB-")
				|| tagg.equals("\''") || tagg.matches("\\``") || tagg
				.equals("\"")) ? "PUNCT" : tagg;
		return POSType.valueOf(taggType).getName();
	}

	// ****
	 private static void printlnOpinionatedMap(
	 Map<String, Pair<Integer, Double>> OpinionatedWordMap)
	 throws UnsupportedEncodingException {
	 PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out,
	 "utf-8"), true);
	
	 for (Map.Entry<String, Pair<Integer, Double>> entry : OpinionatedWordMap
	 .entrySet())
	 pw.printf("\n Word : %25s  count : %5d Score = %3.2f ", entry
	 .getKey(), entry.getValue().getCount(), entry.getValue()
	 .getScore());
	
	 }
	
}

// pw.println (sentence.size());
// pw.println (Sentence.listToString(tSentence, false));