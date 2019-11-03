package cbr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lexicon.Lexicon;
import nlp.TextProcessor;
import text_processor.Document;
import text_processor.WordList;
import classifier.LexiconBasedClassifier;

public class CaseAuthoring {
	int numberOfDiscardedDocuments;

	public List<Case> buildCasesList(List<Document> listOfDocuments,
			List<Lexicon> lexicons, TextProcessor processor) throws IOException {
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();
		List<Case> cases = new ArrayList<Case>();
		int numberOfDiscardedDocuments = 0;
		for (Document document : listOfDocuments) {
			CaseSolution caseSolution = findCaseSolution(document, lexicons,
					classifier, processor); // Dec 9
			if (caseSolution.getLexicons().isEmpty()) {
				numberOfDiscardedDocuments++;
			} else {
				Case oneCase = buildCase(document, caseSolution);
				cases.add(oneCase);
			}
		}
		System.out.printf("\n Number of discarded document is %d ",
				numberOfDiscardedDocuments);
		return cases;
	}

	public Case buildCase(Document document, List<Lexicon> lexicons,
			TextProcessor processor, LexiconBasedClassifier classifier)
			throws Throwable {
		CaseSolution caseSolution = findCaseSolution(document, lexicons,
				classifier, processor); // Dec 9
		if (caseSolution.getLexicons().isEmpty()) {
			numberOfDiscardedDocuments++;
		} else {
			Case oneCase = buildCase(document, caseSolution);
			return oneCase;
		}
		return null;
	}

	private CaseSolution findCaseSolution(Document document,
			List<Lexicon> lexicons, LexiconBasedClassifier classifier,
			TextProcessor processor) throws IOException {
		CaseSolution caseSolution = new CaseSolution();
		WordList wordList = processor.extractWordList(document);
		document.setWordsList(wordList);
		// String fName = document.getName().replace(".text", ".csv");
		// Utility.writeWordListToFile(wordList, fName); // Dec 9
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
		return classifier.predictPolarity(score);
	}

	private Case buildCase(Document document, CaseSolution caseSolution) {
		String name = document.getName();
		CaseDescription caseDescription = document.computeCaseDescription();
		return new Case(name, caseDescription, caseSolution);
	}
}