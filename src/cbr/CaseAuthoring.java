package cbr;

import java.io.IOException;
import java.util.List;

import classifier.LexiconBasedClassifier;
import lexicon.Lexicon;
import nlp.TextProcessor;
import nlp.text_processor.Document;
import nlp.text_processor.WordList;

public class CaseAuthoring {
	private int numberOfDiscardedDocuments = 0;

	public Case buildCase(Document document, List<Lexicon> lexicons,
			TextProcessor processor, LexiconBasedClassifier classifier)
			throws Throwable {
		CaseSolution caseSolution = findCaseSolution(document, lexicons,
				classifier, processor);
		if (caseSolution.getLexicons().size() == 0) {
			numberOfDiscardedDocuments++;
			return null;
		} else {
			return newCase(document, caseSolution);
		}

	}

	private CaseSolution findCaseSolution(Document document,
			List<Lexicon> lexicons, LexiconBasedClassifier classifier,
			TextProcessor processor) throws IOException {
		CaseSolution caseSolution = new CaseSolution();
		WordList wordList = processor.extractWordList(document);
		document.setWordsList(wordList);
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

	private Case newCase(Document document, CaseSolution caseSolution) {
		String name = document.getName();
		CaseDescription caseDescription = document.computeCaseDescription();
		return new Case(name, caseDescription, caseSolution);
	}

	public int getNumberOfDiscardedDocuments() {
		return numberOfDiscardedDocuments;
	}

	// public void setNumberOfDiscardedDocuments(int number) {
	// numberOfDiscardedDocuments = number;
	// }

	// public void write(Document document) throws Throwable {
	// String fName = document.getName().replace(".text", ".csv");
	// Utility.writeWordListToFile(document.getWordsList(), fName);
	// }
}

// public List<Case> buildCasesList(List<Document> listOfDocuments,
// List<Lexicon> lexicons, TextProcessor processor) throws IOException {
// LexiconBasedClassifier classifier = new LexiconBasedClassifier();
// List<Case> cases = new ArrayList<Case>();
// int numberOfDiscardedDocuments = 0;
// for (Document document : listOfDocuments) {
// CaseSolution caseSolution = findCaseSolution(document, lexicons,
// classifier, processor); // Dec 9
// if (caseSolution.getLexicons().isEmpty()) {
// numberOfDiscardedDocuments++;
// } else {
// Case oneCase = buildCase(document, caseSolution);
// cases.add(oneCase);
// }
// }
// System.out.printf("\n Number of discarded document is %d ",
// numberOfDiscardedDocuments);
// return cases;
// }
