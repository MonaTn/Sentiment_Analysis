package xTestUtil;

/**
 * Case-based reasoning classifer
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import lexicon.Lexicon;
import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.CaseSolution;
import shared.Document;
import shared.LexiconBasedClassifier;
import shared.Normalizer;
import shared.Utility;
import shared.WordList;
import Test.SimilarityMeasure;

public class CBR_Classifier {

	private final LexiconBasedClassifier lexiconClassifier = new LexiconBasedClassifier();
	private TextPreprocessor_SnowBall processor;
	private double[] minValues;
	private double[] maxValues;

	public void classify(List<Document> documents, CaseBase caseBaseClassifier)
			throws IOException {
		String path = "";
		String fileName = "";
		List<Vector<Object>> resultsOfPrediction = new ArrayList<Vector<Object>>();
		processor = new TextPreprocessor_SnowBall();
		minValues = caseBaseClassifier.getMinValues();
		maxValues = caseBaseClassifier.getMaxValues();
		for (Document document : documents) {
			setDocumentElements(document);
			List<Lexicon> lexicalSolutions = getSolutions(document,
					caseBaseClassifier);
			Vector<Object> result = predict(document, lexicalSolutions);
			resultsOfPrediction.add(result);
		}
		Utility.writeToFile(resultsOfPrediction, path, fileName);
	}

	private void setDocumentElements(Document document) throws IOException {
		WordList wordList = processor.extractWordList(document);
		document.setWordsList(wordList);
		CaseDescription caseDescription = new CaseDescription(document);
		CaseDescription normalizedCaseDescription = Normalizer
				.normalizeCaseDescription(caseDescription, minValues, maxValues);
		document.setCaseDescription(normalizedCaseDescription);
	}

	private List<Lexicon> getSolutions(Document document, CaseBase caseBase)
			throws IOException {
		Map<Object, Double> similarityMap = computeSimilarity(document,
				caseBase);
		TreeMap<Object, Double> lexiconMap = retrieveLexicons(similarityMap,
				caseBase);
		return extractMostFrequent(lexiconMap);
	}

	private Map<Object, Double> computeSimilarity(Document document,
			CaseBase caseBase) {
		Map<Object, Double> similarity = new HashMap<Object, Double>();
		for (Case oneCase : caseBase.getCases()) {
			double distance = computeDistance(document, oneCase);
			similarity.put(oneCase.getName(), distance);
		}
		return sortAscending(similarity);

	}

	private double computeDistance(Document document, Case oneCase) {
		double[] caseFeatures = oneCase.getFeatures();
		double[] documentFeatures = document.getFeatures();
		return SimilarityMeasure.euclideanDistance(documentFeatures,
				caseFeatures);
	}

	private TreeMap<Object, Double> retrieveLexicons(Map<Object, Double> map,
			CaseBase caseBase) {
		Map<Object, Double> lexiconMap = new HashMap<Object, Double>();
		for (Map.Entry<Object, Double> entry : map.entrySet()) {
			String caseName = (String) entry.getKey();
//			List<Solution> ss = new ArrayList<Solution>();
//			Collections.sort(ss, Solution.Comparators.Measure);
			CaseSolution caseSolution = extractCaseSolution(caseName, caseBase);
			if (caseSolution.equals(null)) {
				System.out.println("Error : " + caseName
						+ " file not found! :(");
			} else {
				addLexiconsToList(caseSolution, lexiconMap);
			}
		}

		System.out.println("lexicon map before sorting "
				+ lexiconMap.toString());
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

	private void addLexiconsToList(CaseSolution caseSolution,
			Map<Object, Double> map) {
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

	private TreeMap<Object, Double> sortAscending(Map<Object, Double> map) {
		AscendingComparator valuComparator = new AscendingComparator(map);
		TreeMap<Object, Double> sortedMap = new TreeMap<Object, Double>(
				valuComparator);
		sortedMap.putAll(map);
		return sortedMap;
	}

	private TreeMap<Object, Double> sortDescending(Map<Object, Double> map) {
		DescendingComparator valuComparator = new DescendingComparator(map);
		TreeMap<Object, Double> sortedMap = new TreeMap<Object, Double>(
				valuComparator);
		sortedMap.putAll(map);
		return sortedMap;
	}

	private Vector<Object> predict(Document document, List<Lexicon> lexicons) {
		double score = lexiconClassifier
				.calculateTotalDocumentScoreByAllLexicons(document, lexicons);
		boolean polarity = lexiconClassifier.predictPolarity(score);
		return buildVector(document, score, polarity, lexicons);
	}

	private Vector<Object> buildVector(Document document, double score,
			boolean polarity, List<Lexicon> lexicons) {
		Vector<Object> result = new Vector<Object>();
		result.addElement(new String(document.getName()));
		result.addElement(new Double(score));
		result.addElement(new Boolean(polarity));
		for (Lexicon lexicon : lexicons) {
			result.addElement(lexicon);
		}
		return result;
	}

}

class DescendingComparator implements Comparator<Object> {

	Map<Object, Double> map;

	public DescendingComparator(Map<Object, Double> map) {
		this.map = map;
	}

	@Override
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

	@Override
	public int compare(Object a, Object b) {
		if (map.get(a) >= map.get(b)) {
			return 1;
		} else {
			return -1;
		}
	}

}
