package classifier;

/**
 * Case-based reasoning classifier
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import lexicon.Lexicon;
import lexicon.LexiconFrequency;
import nlp.TextProcessor;
import nlp.TextProcessorFactory;
import nlp.text_processor.Document;
import nlp.text_processor.SimilarityMeasure;
import nlp.text_processor.WordList;
import utilities.Utility;
import cbr.Case;
import cbr.CaseBase;
import cbr.CaseDescription;
import cbr.CaseNormalizer;
import cbr.retrivaledCase;

public class CBRClassifier {

	private int k = 3;
	private double[] featuresMinValues;
	private double[] featuresMaxValues;
	private LexiconBasedClassifier classifier;
	private TextProcessor processor;

	public CBRClassifier() {
		initialized();
	}

	private void initialized() {
		classifier = new LexiconBasedClassifier();
		TextProcessorFactory factory = new TextProcessorFactory();
		factory.setStemmer("SnowBall_Stemmer");
		processor = factory.create();
		processor.enableNegationAnalysis(false);
	}

	public void classify(CaseBase caseBase, List<Document> documents)
			throws Throwable {
		featuresMinValues = caseBase.getMinValues();
		featuresMaxValues = caseBase.getMaxValues();
		List<Vector<Object>> resultsOfPrediction = new ArrayList<Vector<Object>>();
		for (Document document : documents) {
			Vector<Object> result = prediction(caseBase, document);
			resultsOfPrediction.add(result);
		}
		saveResults(resultsOfPrediction);
	}

	private Vector<Object> prediction(CaseBase caseBase, Document document)
			throws IOException, Throwable {
		setDocumentElements(document);
		List<Lexicon> solution = retrieveAppropriateLexicons(document, caseBase);
		return process(document, solution);
	}

	private void setDocumentElements(Document document) throws IOException {
		WordList wordList = processor.extractWordList(document);
		document.setWordsList(wordList);
		CaseDescription caseDescription = computeCaseDescription(document);
		document.setCaseDescription(caseDescription);
	}

	private CaseDescription computeCaseDescription(Document document) {
		CaseDescription caseDescription = document.computeCaseDescription();
		return CaseNormalizer.caseDescriptionNormalize(caseDescription,
				featuresMinValues, featuresMaxValues);
	}

	private List<Lexicon> retrieveAppropriateLexicons(Document document,
			CaseBase caseBase) throws Throwable {
		List<retrivaledCase> solutions = buildSortedSolutionList(document, caseBase);
		return ranking(solutions);
	}

	private List<retrivaledCase> buildSortedSolutionList(Document document,
			CaseBase caseBase) {
		List<retrivaledCase> solutions = new ArrayList<retrivaledCase>();
		for (Case oneCase : caseBase.getCases()) {
			double distance = computeDistance(document, oneCase);
			solutions.add(new retrivaledCase(oneCase.getCaseSolution(), distance));
		}
		Collections.sort(solutions, retrivaledCase.Comparators.Distance); // Sort-ascending
		return solutions;
	}

	private double computeDistance(Document document, Case oneCase) {
		double[] caseFeatures = oneCase.getCaseDescription().getFeatures(); // remove
																			// method
																			// getFeatures
		double[] documentFeatures = document.getFeatures();
		return SimilarityMeasure.euclideanDistance(documentFeatures,
				caseFeatures);
	}

	private List<Lexicon> ranking(List<retrivaledCase> solutions) throws Throwable {
		if (k <= 0) {
			System.out.println("Should select k greater than 0 !");
			System.exit(0);
		} else if (k == 1) {
			return getFirstCaseSolutions(solutions.get(0));
		} else if (k > 1) {
			return getMostFrequentLexicons(solutions);
		}
		return null;
	}

	private List<Lexicon> getFirstCaseSolutions(retrivaledCase solution)
			throws Exception, Throwable {
		List<Lexicon> lexicons = new ArrayList<Lexicon>();
		for (Lexicon lexicon : solution.getCaseSolution().getLexicons()) {
			System.out.println(lexicon.getClass().getName());
			lexicons.add(lexicon);
		}
		return lexicons;
	}

	private List<Lexicon> getMostFrequentLexicons(List<retrivaledCase> solutions)
			throws Throwable {
		List<LexiconFrequency> lexiconFrequencies = buildFrequencyList(solutions);
		int maxOccurance = lexiconFrequencies.get(0).getCount();
		List<Lexicon> lexicons = new ArrayList<Lexicon>();
		lexicons.add(lexiconFrequencies.get(0).getLexicon());
		for (int i = 1; i < lexiconFrequencies.size(); i++) {
			if (lexiconFrequencies.get(i).getCount() == maxOccurance) {
				lexicons.add(lexiconFrequencies.get(i).getLexicon());
			} else {
				break;
			}
		}
		return lexicons;
	}

	private List<LexiconFrequency> buildFrequencyList(List<retrivaledCase> solutions)
			throws Throwable {
		List<LexiconFrequency> lexiconFrequencies = new ArrayList<LexiconFrequency>();
		for (int i = 0; i < k; i++) {
			List<Lexicon> lexicons = solutions.get(i).getCaseSolution()
					.getLexicons();
			for (Lexicon lexicon : lexicons) {
				int index = getIndexOf(lexiconFrequencies, lexicon);
				if (index == -1) {
					System.out.println(lexicon.getClass().getName());
					lexiconFrequencies.add(new LexiconFrequency(lexicon, 1));
				} else {
					lexiconFrequencies.get(index).increaseCount();
				}
			}
		}
		Collections
				.sort(lexiconFrequencies, LexiconFrequency.Comparators.Count); // Sort-descending
		return lexiconFrequencies;
	}

	private int getIndexOf(List<LexiconFrequency> lexiconFrequencies,
			Lexicon lexicon) {
		for (LexiconFrequency lexiconFrequency : lexiconFrequencies) {
			if (lexiconFrequency.equals(lexicon)) {
				return lexiconFrequencies.indexOf(lexiconFrequency);
			}
		}
		return -1;
	}

	private Vector<Object> process(Document document, List<Lexicon> lexicons) {

		double score = classifier.calculateTotalDocumentScoreByAllLexicons(
				document, lexicons);
		boolean polarity = classifier.predictPolarity(score);
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
		System.out.println(document.getName() + " , " + score + " , "
				+ Arrays.toString(lexicons.toArray()));
		return result;
	}

	private void saveResults(List<Vector<Object>> resultsOfPrediction)
			throws IOException {
		String path = "../../Results/2014/Test/Jan/";
		String fileName = "Apparel_neg_ver2(20140123).csv";
		Utility.writeListOfVectorsToFile(resultsOfPrediction, path, fileName);
	}

}
