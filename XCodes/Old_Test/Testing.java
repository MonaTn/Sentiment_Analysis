package Old_Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;




import lexicon.Lexicon;

import shared.Case;
import shared.CaseBase;
import shared.CaseSolution;
import shared.Document;
import training.LexiconBasedClassifier;

public class Testing {

	private int numberOfNearestCases;
	private LexiconBasedClassifier classifier = new LexiconBasedClassifier();

	public Vector<Object> classify(Document document, CaseBase caseBase, int k)
			throws IOException {
		numberOfNearestCases = k;
		System.out.println("***************************** \n");
		System.out.println(document.getName()+" : "+ Arrays.toString(document.getFeatures()));
		List<Lexicon> lexicons = extractLexicons(document, caseBase);
		
		System.out.println("chosen lexicon : "+lexicons.toString());
		
		return predict(document, lexicons);
	}

	private List<Lexicon> extractLexicons(Document document, CaseBase caseBase)
			throws IOException {
		Map<Object, Double> casesAndDistance = computeSimilarity(document,
				caseBase);
		System.out.println ("result sorted Case and distance :"+ casesAndDistance.toString());
		
		TreeMap<Object, Double> lexiconMap = retrieveLexicons(casesAndDistance,
				caseBase);
		
		System.out.println("After sorting : "+lexiconMap.size());
		System.out.println(lexiconMap.toString());
		
		return extractMostFrequent(lexiconMap);
	}

	private Map<Object, Double> computeSimilarity(Document document,
			CaseBase caseBase) {
		Map<Object, Double> casesAndDistance = new HashMap<Object, Double>();
		for (Case oneCase : caseBase.getCases()) {
			double distance = computeDistance(document, oneCase);
			casesAndDistance.put(oneCase.getName(), distance);
		}
		System.out.println (document.getName()+" with casebase"+ casesAndDistance.toString());
		return sortAscending(casesAndDistance);
		
	}

	private TreeMap<Object, Double> sortAscending(Map<Object, Double> map) {
		AscendingComparator valuComparator = new AscendingComparator(map);
		TreeMap<Object, Double> sortedMap = new TreeMap<Object, Double>(
				valuComparator);
		sortedMap.putAll(map);
		return sortedMap;
	}

	private TreeMap<Object, Double> retrieveLexicons(Map<Object, Double> map,
			CaseBase caseBase) {
		Map<Object, Double> lexiconMap = new HashMap<Object, Double>();
		for (Map.Entry<Object, Double> entry : map.entrySet()) {
			if (numberOfNearestCases == 0) {
				break;
			}
			String caseName = (String) entry.getKey();
			CaseSolution caseSolution = extractCaseSolution(caseName, caseBase);
			if (caseSolution.equals(null)) {
				System.out.println("Error : " + caseName
						+ " file not found! :(");
			} else {
				addLexiconsToList(caseSolution, lexiconMap);
			}
			numberOfNearestCases--;
		}
		
		System.out.println("lexicon map before sorting " + lexiconMap.toString());
		return sortDescending(lexiconMap);
	}

	private CaseSolution extractCaseSolution(String caseName, CaseBase caseBase) {
		for (Case oneCase : caseBase.getCases()) {
			if (oneCase.getName().contains(caseName)) {
				return oneCase.getCaseSolution();
			}
		}
		return null;
	}

	private void addLexiconsToList(CaseSolution caseSolution, Map<Object, Double> map) {
		for (Lexicon lexicon : caseSolution.getLexicons()) {
			double counter = (map.containsKey(lexicon)) ? map.get(lexicon) + 1
					: 1;
			map.put(lexicon, counter);
		}
	}

	private List<Lexicon> extractMostFrequent(TreeMap<Object, Double> map) {
		double MaximumFrequency = map.firstEntry().getValue();
		List<Lexicon> lexicons = new ArrayList<Lexicon>();
		for (Map.Entry<Object, Double> entry : map.entrySet()) {
			if (entry.getValue() == MaximumFrequency) {
				lexicons.add((Lexicon) entry.getKey());
			}
		}
		return lexicons;
	}

	private TreeMap<Object, Double> sortDescending(Map<Object, Double> map) {
		DescendingComparator valuComparator = new DescendingComparator(map);
		TreeMap<Object, Double> sortedMap = new TreeMap<Object, Double>(
				valuComparator);
		sortedMap.putAll(map);
		return sortedMap;
	}

	private double computeDistance(Document document, Case oneCase) {
		double[] caseFeatures = oneCase.getFeatures();
		double[] documentFeatures = document.getFeatures();
		return SimilarityMeasure.euclideanDistance(documentFeatures,
				caseFeatures);
	}

	private Vector<Object> predict(Document document, List<Lexicon> lexicons) {
		double score = classifier.calculateTotalDocumentScoreByAllLexicons(
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

// public void test(CaseBase caseBase) throws Exception {
// List<Document> testDocumentsList = prepareDocumentList();
// for (Document document : testDocumentsList) {
// documentPrepration(document);
// List<Lexicon> lexicons = extractLexicons(document, caseBase);
// Vector<Object> result = predict(document, lexicons);
// resultsOfTestingPhase.add(result);
// }
// writeTofile(resultsOfTestingPhase);
// }
// private List<Document> prepareDocumentList() throws Exception {
// String path = rootPath + subPath;
// List<Document> testDocumentList = DirectoryUtil.getListOfDocument(path,
// false);
// return testDocumentList;
// }

// private void documentPrepration(Document document) throws IOException {
// processor.extractWordsAndSentences(document);
// CaseDescription cd = new CaseDescription(document).normalize(
// normalizer, minValues, maxValues);
// document.setCaseDescription(cd);
// }

// private void writeTofile(List<Vector<Object>> vectors) throws IOException {
// FileUtility fileUtil = new FileUtility();
// File resultFile = fileUtil.createFile(resultPath, resultFileName);
// BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
// resultFile));
// for (Vector<Object> vector : vectors) {
// fileUtil.writeToFile(vector.toString(), bufferedWriter);
// }
// bufferedWriter.close();
// }
