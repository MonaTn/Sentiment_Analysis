package train;

import java.io.IOException;
import java.util.List;

import nlp.TextProcessor;
import lexicon.Lexicon;
import text_processor.Document;
import text_processor.WordList;
import cbr.Case;
import cbr.CaseBase;
import cbr.CaseDescription;
import cbr.CaseSolution;
import classifier.LexiconBasedClassifier;

public class CaseBaseAuthore {

	public CaseBase buildCaseBase(List<Document> listOfDocuments,
			List<Lexicon> lexicons, TextProcessor processor) throws IOException {
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();
		CaseBase caseBase = new CaseBase();
		int numberOfDiscardedDocuments = 0;
		for (Document document : listOfDocuments) {
			CaseSolution caseSolution = findCaseSolution(document, lexicons,
					classifier, processor); // Dec 9
			if (caseSolution.getLexicons().isEmpty()) {
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
