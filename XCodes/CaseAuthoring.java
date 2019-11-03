package training;

/**
 * Last modification 25 February 2013
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import lexicon.Lexicon;

import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.CaseSolution;
import shared.Document;
import shared.LexiconBasedClassifier;
import shared.TextPreprocessor;

public class CaseAuthoring {
	
	private TextPreprocessor processor = new TextPreprocessor();
	private int discardedDocuments;
	
	public CaseBase buildCaseBase(List<Document> trainingDocumentsList,
			List<Lexicon> lexicons) throws Exception, IOException {
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();
		CaseBase caseBase = new CaseBase();
		discardedDocuments = 0;
		for (Document document : trainingDocumentsList) {
			Case oneCase = buildCase(lexicons, classifier, document);
			if (oneCase != null) {
				caseBase.add(oneCase);
			} else {
				discardedDocuments++;
			}
		}
		System.out.printf("\n Number of discarded document is %d ",
				discardedDocuments); 
		return caseBase;
	}

	private Case buildCase(List<Lexicon> lexicons, LexiconBasedClassifier classifier, Document document)
			throws ClassNotFoundException, IOException,
			UnsupportedEncodingException {
		CaseSolution caseSolution = new CaseSolution();
		processor.extractWordsAndSentences(document);
//		 String path = "../../Results/2013/Feb/2502.013/Apparel/";
//		 document.writeWordsListToFile(path);
		predictPolarity(lexicons, classifier, document, caseSolution);
		return addToCaseBase(document, caseSolution);
	}

	private void predictPolarity(List<Lexicon> lexicons,
			LexiconBasedClassifier classifier, Document document,
			CaseSolution caseSolution) throws UnsupportedEncodingException {
		for (Lexicon lexicon : lexicons) {
			boolean obtainedPolarity = predictDocumentPolarity(classifier,
					document, lexicon);
			if (obtainedPolarity == document.getPolarity()) {
				caseSolution.add(lexicon);
			}
		}
	}

	private Case addToCaseBase(Document document, CaseSolution caseSolution)
			throws ClassNotFoundException, IOException {
		if (!caseSolution.getLexicons().isEmpty()) {
			computeCaseDescription(document);
			return new Case(document.getName(), document.getCaseDescription(),
					caseSolution);
		} else {
			return null;
		}
	}

	private boolean predictDocumentPolarity(LexiconBasedClassifier classifier,
			Document document, Lexicon lexicon)
			throws UnsupportedEncodingException {
		double score = classifier.computeTotalScoreOfOpinionatedWords(document,
				lexicon);
		return classifier.predictPolarity(score);
	}

	public void computeCaseDescription(Document document)
			throws ClassNotFoundException, IOException {
		CaseDescription caseDescription = new CaseDescription(document);
		document.setCaseDescription(caseDescription);
	}

}
