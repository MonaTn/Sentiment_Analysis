package training;

/**
 * modification 4 March 2013
 * Last modification 9 December 2013:
 * 	import TextPreprocessor_SnowBall as a parameter
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;



import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.CaseSolution;
import shared.Document;
import shared.FileUtility;
import shared.TextProcessor;
import shared.Utility;
import shared.Word;
import shared.WordList;
import xTestUtil.TextPreprocessor_SnowBall;

import lexicon.Lexicon;

public class CaseAuthoring {

	private int numberOfDiscardedDocuments;
	private String scoreAndSolution;

	public CaseBase buildCaseBase(List<Document> listOfDocuments,
			List<Lexicon> lexicons, TextProcessor processor) throws IOException { // Dec 9
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();
		CaseBase caseBase = new CaseBase();
		numberOfDiscardedDocuments = 0;
		for (Document document : listOfDocuments) {
			CaseSolution caseSolution = findCaseSolution(document, lexicons,
					classifier, processor); // Dec 9
			if (caseSolution.getLexicons().isEmpty()) {
//				bw.write(document.getName() + ", " + scoreAndSolution);
//				bw.newLine();
				numberOfDiscardedDocuments++;
			} else {
				Case oneCase = buildCase(document, caseSolution);
				caseBase.add(oneCase);
			}
		}
		System.out.printf("\n Number of discarded document is %d ",
				numberOfDiscardedDocuments);
		return caseBase;
	}

	private CaseSolution findCaseSolution(Document document,
			List<Lexicon> lexicons, LexiconBasedClassifier classifier,TextProcessor processor)
			throws IOException {
		CaseSolution caseSolution = new CaseSolution();
		WordList wordList = processor.extractWordList(document);
		document.setWordsList(wordList);
		Utility.writeToFile(wordList, document.getName()); // Dec 9
		scoreAndSolution = "";
		for (Lexicon lexicon : lexicons) {
			boolean obtainedPolarity = predictDocumentPolarity(classifier,
					document, lexicon);
			if (obtainedPolarity == document.getPolarity()) {
				caseSolution.add(lexicon);
			}
		}
		return caseSolution;
	}

	private boolean predictDocumentPolarity(LexiconBasedClassifier classifier,
			Document document, Lexicon lexicon) {
		double score = classifier.calculateDocumentScore(document, lexicon);
		scoreAndSolution = scoreAndSolution + ", " + lexicon.toString() + ", "
				+ score + ", ";
		return classifier.predictPolarity(score);
	}

	public Case buildCase(Document document, CaseSolution caseSolution) {
		String name = document.getName();
		CaseDescription caseDescription = computeCaseDescription(document);
		return new Case(name, caseDescription, caseSolution);
	}

	public CaseDescription computeCaseDescription(Document document) {
		return new CaseDescription(document);
	}

}
