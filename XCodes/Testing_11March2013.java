package testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import lexicon.Lexicon;

import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.CaseSolution;
import shared.DirectoryUtil;
import shared.Document;
import shared.FileUtility;
import shared.LexiconBasedClassifier;
import shared.TextPreprocessor;

public class Testing_11March2013 {

	private String rootPath;
	private String subPath;
	private String resultPath;
	private String resultFileName;
	private double[] minValues;
	private double[] maxValues;
	private final int k = 3;

	private List<Vector<Object>> resultsOfTestingPhase = new ArrayList<Vector<Object>>();
	private NormalizationMethod normalizer = new NormalizationMethod();
	private TextPreprocessor processor = new TextPreprocessor();
	private LexiconBasedClassifier classifier = new LexiconBasedClassifier();

	public void test(CaseBase caseBase) throws Exception {
		extractMinMaxValuesOfCaseBase(caseBase);
		List<Document> testDocumentsList = prepareDocumentList();
		for (Document document : testDocumentsList) {
			documentPrepration(document);
			List<Lexicon> lexicons = extractLexicons(document, caseBase);
			Vector<Object> result = classify(document, lexicons);
			resultsOfTestingPhase.add(result);
		}
		writeTofile(resultsOfTestingPhase);
	}

	private void extractMinMaxValuesOfCaseBase(CaseBase caseBase) {
		minValues = caseBase.getMinValues();
		maxValues = caseBase.getMaxValues();
	}

	private List<Document> prepareDocumentList() throws Exception {
		String path = rootPath + subPath;
		List<Document> testDocumentList = DirectoryUtil.getListOfDocument(path,
				false);
		return testDocumentList;
	}

	private List<Lexicon> extractLexicons(Document document, CaseBase caseBase)
			throws IOException {
		Map<Object, Double> casesAndDistance = computeSimilarity(document,
				caseBase);

		retrieveKNearestCases(casesAndDistance, caseBase);
		return null;
	}

	private Map<Object, Double> computeSimilarity(Document document,
			CaseBase caseBase) {
		Map<Object, Double> casesAndDistance = new HashMap<Object, Double>();
		for (Case oneCase : caseBase.getCases()) {
			double distance = computeDistance(document, oneCase);
			casesAndDistance.put(oneCase.getName(), distance);
		}
		return sorsAscending(casesAndDistance);
	}

	private Map<Object, Double> retrieveKNearestCases(
			TreeMap<Object, Double> map, CaseBase caseBase) {
		Map<Object, Double> lexiconMap = new HashMap<Object, Double>();
		for (Map.Entry<Object, Double> entry : map.entrySet()) {
			String caseName = (String) entry.getKey();
			CaseSolution cs = extractCaseSolution(caseName, caseBase);
			if (cs.equals(null)) {
				System.out.println("Error : " + caseName
						+ " file nor found! :(");
			} else {
				addLexiconsToList(cs, lexiconMap);
			}
		}
		TreeMap<Object, Double> sortedMap = sortDscending(lexiconMap);
		List<Lexicon> lexicons = extractMostFrequent(sortedMap);
	}

	private List<Lexicon> extractMostFrequent(TreeMap<Object, Double> map) {
		double MaximumFrequency = map.firstEntry().getValue();
		List<Lexicon> lexicons = new ArrayList<Lexicon>();
		lexicons.add((Lexicon) map.firstEntry().getKey());
		for (Map.Entry<Object, Double> entry : map.entrySet()) {
			if (entry.getValue() == MaximumFrequency) {
				lexicons.add((Lexicon) entry.getKey());
			}
		}
		return lexicons;
	}

	private TreeMap<Object, Double> sortDscending(Map<Object, Double> map) {
		DescendingComparator valuComparator = new DescendingComparator(map);
		TreeMap<Object, Double> sortedMap = new TreeMap<Object, Double>(
				valuComparator);
		sortedMap.putAll(map);
		return sortedMap;
	}

	private void addLexiconsToList(CaseSolution cs, Map<Object, Double> map) {
		for (Lexicon lexicon : cs.getLexicons()) {
			double counter = (map.containsKey(lexicon)) ? map.get(lexicon) + 1
					: 1;
			map.put(lexicon, counter);
		}
	}

	private CaseSolution extractCaseSolution(String caseName, CaseBase caseBase) {
		for (Case oneCase : caseBase.getCases()) {
			if (oneCase.getName().contains(caseName)) {
				return oneCase.getCaseSolution();
			}
		}
		return null;
	}

	private TreeMap<Object, Double> sorsAscending(
			Map<Object, Double> similarCases) {
		AscendingComparator valuComparator = new AscendingComparator(
				similarCases);
		TreeMap<Object, Double> sortedMap = new TreeMap<Object, Double>(
				valuComparator);
		sortedMap.putAll(similarCases);
		return sortedMap;
	}

	private void documentPrepration(Document document) throws IOException {
		processor.extractWordsAndSentences(document);
		CaseDescription cd = new CaseDescription(document).normalize(
				normalizer, minValues, maxValues);
		document.setCaseDescription(cd);
	}

	private double computeDistance(Document document, Case oneCase) {
		double[] caseFeatures = oneCase.getFeatures();
		double[] documentFeatures = document.getFeatures();
		return SimilarityMeasure.euclideanDistance(documentFeatures,
				caseFeatures);
	}

	private Vector<Object> classify(Document document, List<Lexicon> lexicons) {
		double score = classifier.calculateDocumentTotalScoreFromLexicons(
				document, lexicons);
		boolean polarity = classifier.predictPolarity(score);
		return buildVector(document, score, polarity, lexicons);
	}

	private Vector<Object> buildVector(Document document, double score,
			boolean polarity, List<Lexicon> lexicons) {
		Vector<Object> vector = new Vector<Object>();
		vector.addElement(new String(document.getName()));
		vector.addElement(new Double(score));
		vector.addElement(new Boolean(polarity));
		for (Lexicon lexicon : lexicons) {
			vector.addElement(lexicon);
		}
		return vector;
	}

	private void writeTofile(List<Vector<Object>> vectors) throws IOException {
		FileUtility fileUtil = new FileUtility();
		File resultFile = fileUtil.createFile(resultPath, resultFileName);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				resultFile));
		for (Vector<Object> vector : vectors) {
			fileUtil.writeToFile(vector.toString(), bufferedWriter);
		}
		bufferedWriter.close();
	}

	public void setBaseTestSetPath(String baseTestSetPath) {
		this.rootPath = baseTestSetPath;
	}

	public void setSubTestSetPath(String subTestSetPath) {
		this.subPath = subTestSetPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	public void setResultOfTestFiles(String resultOfTestFiles) {
		this.resultFileName = resultOfTestFiles;
	}

	public Vector<Object> classify(Document  documet,CaseBase normalizedCaseBase) {
		return null;
	}

}

class DescendingComparator implements Comparator<Object> {

	Map<Object, Double> map;

	public DescendingComparator(Map<Object, Double> map) {
		this.map = map;
	}

	public int compare(Object a, Object b) {
		if (map.get(a) >= map.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}
}

class AscendingComparator implements Comparator<Object> {

	Map<Object, Double> map;

	public AscendingComparator(Map<Object, Double> map) {
		this.map = map;
	}

	public int compare(Object a, Object b) {
		if (map.get(a) >= map.get(b)) {
			return 1;
		} else {
			return -1;
		}
	}
}