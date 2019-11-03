
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lexicon.Lexicon;

import shared.Case;
import shared.CaseAuthoring;
import shared.CaseBase;
import shared.CaseDescription;
import shared.CaseSolution;
import shared.DirectoryUtil;
import shared.Document;
import shared.LexiconBasedClassifier;
import shared.Serialization;
import shared.TextPreprocessor;

public class CopyOfMain_Test {

	private static String serializedCaeBaseFile = "CaseBase_All-Apparel.ser"; // ?
	private static String baseTestSetPath = "../../TrainingSet";
	private static String subTestSetPath = "/Apparel/pos_Apl/";
	private static String resultPath = "../../Results/2013/feb/25.02.013/";
	private static String resultOfTestFiles = "PredictedTestDocument_Apl_pos_2502013.csv";
	private static int k = 3;
	private static double[] minValues;
	private static double[] maxValues;
	private final static int lowerRange = 0;
	private final static int upperRange = 10;
	static  normalizer = new Normalize();
	static CaseAuthoring caseConstructor;
	static SimilarityMeasure measure;
	static Map<Case, Double> caseMap;

	public static void main(String[] args) throws Exception {
		List<Document> testDocumentList = prepareDocumentList();
		CaseBase caseBase = prepareCaseBase();
		CaseBase normalizedCaseBase = normalizedCaseBase(caseBase);
		caseConstructor = new CaseAuthoring();
		measure = new SimilarityMeasure();
		caseMap = new HashMap<Case, Double>();
		for (Document document : testDocumentList) {
			CaseDescription caseDescription = extractCaseDescription(document);
			double[] features = caseDescription.getFeatures();
			double[] normalizedFeatures = normalizer.normalizeArray(features,
					minValues, maxValues, lowerRange, upperRange);
			document.setFeatures(normalizedFeatures);
			computeSimilarity(document, normalizedCaseBase);
			sortMap(caseMap);
			CaseBase nearestCases = retrivedK_NearestCases(caseMap, k);
			Map<Lexicon, Integer> lexiconMap = rankingCaseSolution(nearestCases);
			selectMostFrequetLexicon();
			computeDocumentScore(lexicons);
			predictdocumentPolarity(score);

		}
	}

	private static Map<Lexicon, Integer> rankingCaseSolution(CaseBase caseBase) {
		Map<Lexicon, Integer> lexiconMap = new HashMap<Lexicon, Integer>();
		for (Case oneCase : caseBase.getCases()) {
			for (Lexicon lexicon : oneCase.getCaseSolution().getLexicons()) {
				int count = (lexiconMap.containsKey(lexicon)) ? lexiconMap.get(lexicon) + 1 : 1;
				lexiconMap.put(lexicon, count);
			}
		}
		return lexiconMap;
	}
	
	private CaseSolution mostfrequentLexicon (Map<Lexicon, Integer> map) {
		
	}
	
	private static CaseDescription extractCaseDescription(Document document) {
		return caseConstructor.computeCaseDescription(document);
	}

	private static void computeSimilarity(Document document, CaseBase caseBase) {
		double[] documentFeatures = document.getFeatures();
		for (Case oneCase : caseBase.getCases()) {
			double[] caseFeatures = oneCase.getFeatures();
			double distance = measure.euclideanDistance(documentFeatures,
					caseFeatures);
			caseMap.put(oneCase, distance);
		}
	}

	private static CaseBase retrivedK_NearestCases(Map<Case, Double> caseMap,
			int k) {
		CaseBase caseBase = new CaseBase();
		for (Case oneCase : caseMap.keySet()) {
			if (k > 0) {
				caseBase.add(oneCase);
				k--;
			}
		}
		return caseBase;
	}

	private static void test() throws Exception, IOException {
		Normalize normalizer = normalizedCaseBase();

		CaseBase normalizedCaseBase = normalizer.normalizeCaseBase(caseBase,
				minValues, maxValues);
		TextPreprocessor processor = new TextPreprocessor();
		CaseAuthoring caseConstructor = new CaseAuthoring();
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();
		RetrivalCases retriever = new RetrivalCases();

		List<PredictedDocument_2013March> predictedDocuments = new ArrayList<PredictedDocument_2013March>();

		for (Document testDocument : testDocumentList) {
			processor.extractWordsAndSentences(testDocument);
			caseConstructor.computeCaseDescription(testDocument);
			testDocument.setCaseDescription(normalizer.minMaxNormalization(
					testDocument.getCaseDescription().getFeatures(), minValues,
					maxValues));
			CaseBase retrievedCaseBase = retriever.retrievedKNearestCases(
					testDocument, normalizedCaseBase, k);
			CaseSolution caseSolution = TestCaseAuthoring
					.buildDocumentCaseSolution(retrievedCaseBase);
			double totalScore = classifier
					.calculateDocumentTotalScoreFromLexicons(testDocument,
							caseSolution);
			boolean polarity = classifier.predictPolarity(totalScore);
			testDocument.setPolarity(polarity);
			predictedDocuments.add(new PredictedDocument_2013March(testDocument,
					totalScore, caseSolution));
		}
		System.out.println(predictedDocuments.size());
		PredictedDocument_2013March.writeToFile(predictedDocuments, resultPath,
				resultOfTestFiles);
	}

	private static List<Document> prepareDocumentList() throws Exception {
		String path = baseTestSetPath + subTestSetPath;
		List<Document> testDocumentList = DirectoryUtil.getListOfDocument(path,
				true);
		return testDocumentList;
	}

	private static CaseBase prepareCaseBase() throws IOException {
		String serializedFilename = resultPath + serializedCaeBaseFile;
		CaseBase caseBase = (CaseBase) Serialization
				.deSerialize(serializedFilename);
		minValues = caseBase.extractMinValues();
		maxValues = caseBase.extractMaxValues();
		return caseBase;
	}

	private static CaseBase normalizedCaseBase(CaseBase caseBase) {
		CaseBase normalizedCaseBase = new CaseBase();
		for (Case oneCase : caseBase.getCases()) {
			Case normalizedCase = normalizedCase(oneCase);
			normalizedCaseBase.add(normalizedCase);
		}
		return normalizedCaseBase;
	}

	private static Case normalizedCase(Case oneCase) {
		double[] features = oneCase.getCaseDescription().getFeatures();
		double[] normalizedFeatures = normalizer.normalizeArray(features,
				minValues, maxValues, lowerRange, upperRange);
		Case normalizedCase = buildNormalizedCase(oneCase, normalizedFeatures);
		return normalizedCase;
	}

	private static Case buildNormalizedCase(Case oneCase, double[] newfeatures) {
		CaseDescription normalizedCaseDescription = new CaseDescription(
				newfeatures);
		return new Case(oneCase.getName(), normalizedCaseDescription,
				oneCase.getCaseSolution());
	}

	public static Map<Case, Double> sortMap(Map<Case, Double> map) { // ?
		AscendingComparator doubleComparator = new AscendingComparator(map);
		TreeMap<Case, Double> sortedMap = new TreeMap<Case, Double>(
				doubleComparator);
		sortedMap.putAll(map);
		
		return sortedMap;
	}
}

class AscendingComparator implements Comparator<Case> {

	Map<Case, Double> map;

	public AscendingComparator(Map<Case, Double> map) {
		this.map = map;
	}

	public int compare(Case a, Case b) {
		if (map.get(a) >= map.get(b)) {
			return 1;
		} else {
			return -1;
		}
	}
}
class DscendingComparator1 implements Comparator<Lexicon> {

	Map<Lexicon, Integer> map;

	public DscendingComparator1(Map<Lexicon, Integer> map) {
		this.map = map;
	}

	public int compare(Lexicon a, Lexicon b) {
		if (map.get(a) >= map.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}

}