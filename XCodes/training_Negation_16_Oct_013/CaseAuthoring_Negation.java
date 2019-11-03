package training_Negation;

/**
 * Last modification 4 March 2013
 */

import java.io.IOException;
import java.util.List;

import Classifier_Negation.LexiconBasedClassifier_Negation;
import Classifier_Negation.NegationPreprocessor_MinMinStrategy;


import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.CaseSolution;
import shared.Document;

import lexicon.Lexicon;

public class CaseAuthoring_Negation {

	private int numberOfDiscardedDocuments;
	private NegationPreprocessor_MinMinStrategy processor = new NegationPreprocessor_MinMinStrategy();

	public CaseBase buildCaseBase(List<Document> listOfDocuments, List<Lexicon> lexicons) throws IOException {
		LexiconBasedClassifier_Negation classifier = new LexiconBasedClassifier_Negation();
		CaseBase caseBase = new CaseBase();
		numberOfDiscardedDocuments = 0;
		for (Document document : listOfDocuments) {
			CaseSolution caseSolution = findCaseSolution(document, lexicons,
					classifier);
			if (caseSolution.getLexicons().isEmpty()) {
				numberOfDiscardedDocuments++;
			} else {
				Case oneCase = buildCase(document, caseSolution);
				caseBase.add(oneCase);
			}
		}
		System.out.printf("\n ** Negation **- Number of discarded document is %d ",
				numberOfDiscardedDocuments);
		return caseBase;
	}

	private CaseSolution findCaseSolution(Document document,
			List<Lexicon> lexicons, LexiconBasedClassifier_Negation classifier)
			throws IOException {
		CaseSolution caseSolution = new CaseSolution();
		processor.extractWordsListOfDocument(document);
		
		for (Lexicon lexicon : lexicons) {
			boolean obtainedPolarity = predictDocumentPolarity(classifier,
					document, lexicon);
			if (obtainedPolarity == document.getPolarity()) {
				caseSolution.add(lexicon);
			}
		}
		return caseSolution;
	}

	private boolean predictDocumentPolarity(LexiconBasedClassifier_Negation classifier,
			Document document, Lexicon lexicon) {
		double score = classifier.calculateDocumentScore(document, lexicon);
		return classifier.predictPolarity(score);
	}

	public Case buildCase(Document document, CaseSolution caseSolution) {
		String name = document.getName();
		CaseDescription caseDescription = computeCaseDescription(document);
		return new Case(name, caseDescription, caseSolution);
	}

	public CaseDescription computeCaseDescription(Document document) {
		return  new CaseDescription(document);
	}

}
