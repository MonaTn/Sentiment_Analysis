package Test;

/**
 * Case-based reasoning classifier
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import lexicon.GeneralInquirer;
import lexicon.Lexicon;
import lexicon.MSOL;
import lexicon.NRC;
import lexicon.SentiWordNet;
import lexicon.SubjectivityClues;
import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.Document;
import shared.FileUtility;
import shared.LexiconBasedClassifier;
import shared.Normalizer;
import shared.Utility;
import shared.WordList;
import xTestUtil.NegationTextPreprocessor_SnowBall;

public class CBR_Classifier_ByNegation {

	private int k = 2;
	private double[] minValues;
	private double[] maxValues;
	private List<Lexicon> allLexicons;
	private BufferedWriter bufferedWriter;

	private static LexiconBasedClassifier lexiconClassifier;
	private static NegationTextPreprocessor_SnowBall processor;

	public void classify(List<Document> documents, CaseBase caseBase)
			throws Throwable {
		initializeClassifier();
		CaseBase caseBaseClassifier = preprocessCaseBase(caseBase);
		List<Vector<Object>> resultsOfPrediction = new ArrayList<Vector<Object>>();
		for (Document document : documents) {
			setDocumentElements(document);
			List<Lexicon> finalSolution = getLexicalSolutions(document,
					caseBaseClassifier);
			Vector<Object> result = predict(document, finalSolution);
			resultsOfPrediction.add(result);
			if (resultsOfPrediction.size() == 50) {
				Utility.writeToFile(resultsOfPrediction, bufferedWriter);
				resultsOfPrediction.clear();
			}
		}
		bufferedWriter.close();
	}

	private void initializeClassifier() throws Exception {
		lexiconClassifier = new LexiconBasedClassifier();
		processor = new NegationTextPreprocessor_SnowBall();
		String path = "../../Results/2013/Nov/29/by Negation/";
		String fileName = "test1.csv";
		File resultFile = FileUtility.createFile(path, fileName);
		bufferedWriter = new BufferedWriter(new FileWriter(resultFile));
		buildListOfAllLexicons();
	}

	private void buildListOfAllLexicons() { // To avoid to make newInstance of each lexicon per document 
		allLexicons = new ArrayList<Lexicon>();
		allLexicons.add(new SentiWordNet());
		allLexicons.add(new MSOL());
		allLexicons.add(new GeneralInquirer());
		allLexicons.add(new SubjectivityClues());
		allLexicons.add(new NRC());
	}

	private CaseBase preprocessCaseBase(CaseBase caseBase) { // ok
		minValues = caseBase.getMinValues();
		maxValues = caseBase.getMaxValues();
		return Normalizer.normalizeCaseBase(caseBase, minValues, maxValues);
	}

	private void setDocumentElements(Document document) throws IOException { // ok
		WordList wordList = processor.extractWordList(document);
		document.setWordsList(wordList);
		CaseDescription caseDescription = new CaseDescription(document);
		CaseDescription normalizedCaseDescription = Normalizer
				.normalizeCaseDescription(caseDescription, minValues, maxValues);
		document.setCaseDescription(normalizedCaseDescription);
	}

	private List<Lexicon> getLexicalSolutions(Document document,
			CaseBase caseBase) throws Throwable {
		List<Solution> sortedSolutionsBySimilarity = computeSimilarity(
				document, caseBase);
		return retrieveTrueLexicons(sortedSolutionsBySimilarity);
	}

	private List<Solution> computeSimilarity(Document document,
			CaseBase caseBase) { // Ok
		List<Solution> solutions = new ArrayList<Solution>();
		for (Case oneCase : caseBase.getCases()) {
			double distance = computeDistance(document, oneCase);
			solutions.add(new Solution(oneCase.getCaseSolution(), distance));
		}
		Collections.sort(solutions, Solution.Comparators.Distance); // Sort-ascending
		return solutions;
	}

	private double computeDistance(Document document, Case oneCase) {
		double[] caseFeatures = oneCase.getFeatures();
		double[] documentFeatures = document.getFeatures();
		return SimilarityMeasure.euclideanDistance(documentFeatures,
				caseFeatures);
	}

	private List<Lexicon> retrieveTrueLexicons(List<Solution> solutions)
			throws Throwable {
		if (k <= 0) {
			System.out.println("Should select k greater than 0 !");
			System.exit(0);
		} else if (k == 1) {
			return solutions.get(0).getCaseSolution().getLexicons();
		} else if (k > 1) {
			return mostFrequentLexicons(solutions);
		}
		return null;
	}

	private List<Lexicon> mostFrequentLexicons(List<Solution> solutions)
			throws Throwable {
		List<LexiconFrequency> sortedLexiconCountList = builSortedList(solutions);
		int maxOccurance = sortedLexiconCountList.get(0).getCount();
		List<Lexicon> lexicalSolutions = new ArrayList<Lexicon>();
		lexicalSolutions.add(sortedLexiconCountList.get(0).getLexicon());
		for (int i = 1; i < sortedLexiconCountList.size(); i++) {
			if (sortedLexiconCountList.get(i).getCount() == maxOccurance) {
				lexicalSolutions
						.add(sortedLexiconCountList.get(i).getLexicon().getClass().newInstance());
			} else {
				break;
			}
		}
		return lexicalSolutions;
	}

	private List<LexiconFrequency> builSortedList(List<Solution> solutions)
			throws Throwable {
		List<LexiconFrequency> listOfLexiconWithCount = new ArrayList<LexiconFrequency>();
		for (int i = 0; i < k; i++) {
			List<Lexicon> lexiconListOfSolution = solutions.get(i)
					.getCaseSolution().getLexicons();
			for (Lexicon lexicon : lexiconListOfSolution) {
				int index = getIndexOf(listOfLexiconWithCount, lexicon);
				if (index == -1) {
					listOfLexiconWithCount.add(new LexiconFrequency(lexicon
							.getClass().newInstance(), 1));
				} else {
					listOfLexiconWithCount.get(index).increaseCount();
				}
			}
		}
		Collections
				.sort(listOfLexiconWithCount, LexiconFrequency.Comparators.Count); // Sort-descending
		return listOfLexiconWithCount;
	}

	private int getIndexOf(List<LexiconFrequency> lexiconContList, Lexicon lexicon) {
		for (LexiconFrequency lexiconCount : lexiconContList) {
			if (lexiconCount.getLexicon().getClass().getName()
					.equals(lexicon.getClass().getName())) {
				return lexiconContList.indexOf(lexiconCount);
			}
		}
		return -1;
	}

	private Vector<Object> predict(Document document, List<Lexicon> lexicons) {
		double totalScoreOfDocument = 0;
		for (Lexicon lexiconSolution : lexicons) {
			for (Lexicon lexicon : allLexicons) {
				if (lexicon.getClass().getName()
						.equals(lexiconSolution.getClass().getName())) {
					totalScoreOfDocument += lexiconClassifier.calculateDocumentScore(
							document, lexicon);
					break;
				}
			}
		}
//		 double score = lexiconClassifier
//		 .calculateTotalDocumentScoreByAllLexicons(document, lexicons);
		boolean polarity = lexiconClassifier
				.predictPolarity(totalScoreOfDocument);
		return buildVector(document, totalScoreOfDocument, polarity, lexicons);
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

}
