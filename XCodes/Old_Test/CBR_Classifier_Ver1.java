package Test;

/**
 * Case-based reasoning classifier
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;


import lexicon.Lexicon;
import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.Document;
import shared.Normalizer;
import shared.Utility;
import shared.WordList;
import training.LexiconBasedClassifier;
import xTestUtil.TextPreprocessor_SnowBall;

public class CBR_Classifier_Ver1 {

	private int k = 3;
	private double[] minValues;
	private double[] maxValues;
	private static LexiconBasedClassifier lexiconClassifier;
	private static TextPreprocessor_SnowBall processor;
	
	public void classify(List<Document> documents, CaseBase caseBaseClassifier)
			throws IOException {
		String path = "../../Results/Nov/29/test/";
		String fileName = "testOnMovie_neg_ByNegation.csv";
		lexiconClassifier = new LexiconBasedClassifier();
		processor = new TextPreprocessor_SnowBall();
		List<Vector<Object>> resultsOfPrediction = new ArrayList<Vector<Object>>();
		minValues = caseBaseClassifier.getMinValues();
		maxValues = caseBaseClassifier.getMaxValues();
		for (Document document : documents) {
			setDocumentElements(document);
			List<Lexicon> lexicalSolutions = getLexicalSolutions(document,
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

	private List<Lexicon> getLexicalSolutions(Document document, CaseBase caseBase)
			throws IOException {
		List<Solution> sortedSolutionsBySimilarity = computeSimilarity(
				document, caseBase);
		return retrieveTrueLexicons(sortedSolutionsBySimilarity);
	}

	private List<Solution> computeSimilarity(Document document,
			CaseBase caseBase) {
		List<Solution> solutions = new ArrayList<Solution>();
		for (Case oneCase : caseBase.getCases()) {
			double distance = computeDistance(document, oneCase);
			solutions.add(new Solution(oneCase.getCaseSolution(), distance));
		}
		Collections.sort(solutions, Solution.Comparators.Distance); // Sort ascending
		return solutions;
	}

	private double computeDistance(Document document, Case oneCase) {
		double[] caseFeatures = oneCase.getFeatures();
		double[] documentFeatures = document.getFeatures();
		return SimilarityMeasure.euclideanDistance(documentFeatures,
				caseFeatures);
	}

	private List<Lexicon> retrieveTrueLexicons(List<Solution> solutions) {
		if (k <= 0) {
			System.out.println("Should select k greater than 0 !");
			System.exit(0);
		} else if (k == 1) {
			return solutions.get(0).getCaseSolution().getLexicons();
		} else {
			return mostFrequentLexicons(solutions);
		}
		return null;
	}

	private List<Lexicon> mostFrequentLexicons(List<Solution> solutions) {
		List<LexiconCount> sortedLexiconCountList = builSortedList(solutions);
		int maxOccurance = sortedLexiconCountList.get(0).getCount();
		List<Lexicon> lexicalSolutions = new ArrayList<Lexicon>();
		lexicalSolutions.add(sortedLexiconCountList.get(0).getLexicon());
		int i = 1;
		while (sortedLexiconCountList.get(i).getCount() == maxOccurance) {
			lexicalSolutions.add(sortedLexiconCountList.get(i).getLexicon());
			i++;
		}
		return lexicalSolutions;
	}

	private List<LexiconCount> builSortedList(List<Solution> solutions) {
		List<LexiconCount> lexiconWithCount = new ArrayList<LexiconCount>();
		for (int i = 0; i < k; i++) {
			List<Lexicon> lexiconList = solutions.get(i).getCaseSolution()
					.getLexicons();
			for (Lexicon lexicon : lexiconList) {
				addOrUpdateList(lexiconWithCount, lexicon);
			}
		}
		Collections.sort(lexiconWithCount, LexiconCount.Comparators.Count); // Sort descending
		return lexiconWithCount;
	}

	private void addOrUpdateList(List<LexiconCount> lexiconWithCount,
			Lexicon lexicon) {
		for (LexiconCount lexiconAndCount : lexiconWithCount) {
			if (lexiconAndCount.getLexicon().getClass().getName()
					.equals(lexicon.getClass().getName())) {
				lexiconAndCount.increaseCount();
			} else {
				lexiconWithCount.add(new LexiconCount(lexicon, 1));
			}
		}
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

