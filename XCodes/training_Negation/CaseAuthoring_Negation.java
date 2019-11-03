package training_Negation;

/**
 * Last modification 16 October 2013
 */

//import java.io.BufferedWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.CaseSolution;
import shared.Document;
import shared.FileUtility;
import shared.LexiconBasedClassifier;
import shared.Utility;
import shared.Word;
import shared.WordList;
import xTestUtil.NegationTextPreprocessor_SnowBall;

import lexicon.Lexicon;

public class CaseAuthoring_Negation {

	private int numberOfDiscardedDocuments;
	private NegationTextPreprocessor_SnowBall processor;
	
//	private BufferedWriter bw;
//	private double score1 = 0.0;

	public CaseBase buildCaseBase(List<Document> listOfDocuments,
			List<Lexicon> lexicons) throws Exception {
		processor = new NegationTextPreprocessor_SnowBall();
		LexiconBasedClassifier classifier = new LexiconBasedClassifier();
		CaseBase caseBase = new CaseBase();
		numberOfDiscardedDocuments = 0;
		
//		bw = new BufferedWriter(new FileWriter(FileUtility.createFile("../../Results/2013/NOV/28/","Neg_AllButMusic_Discarded.csv")));
		
		for (Document document : listOfDocuments) {
			CaseSolution caseSolution = findCaseSolution(document, lexicons,
					classifier);
			if (caseSolution.getLexicons().isEmpty()) {
//				bw.write(document.getName()+", "+ score1);
//				bw.newLine();
				numberOfDiscardedDocuments++;
			} else {
				Case oneCase = buildCase(document, caseSolution);
				caseBase.add(oneCase);
			}
		
		}
		System.out.printf(
				"\n ** By Negation **- Number of discarded document is %d ",
				numberOfDiscardedDocuments);
//		bw.close();
		return caseBase;
	}

	private CaseSolution findCaseSolution(Document document,
			List<Lexicon> lexicons, LexiconBasedClassifier classifier) throws IOException {
		CaseSolution caseSolution = new CaseSolution();
		WordList wordList = processor.extractWordList(document);
		document.setWordsList(wordList);
		Utility.writeToFile(wordList, document.getName()); //

//		for (Word word:wordList.getWords()) {
////			bw.write(word.getWord()+","+word.getTag()+", "+word.getCount());
////			bw.newLine();
//		}
		for (Lexicon lexicon : lexicons) {
			boolean obtainedPolarity = predictDocumentPolarity(classifier,
					document, lexicon);
			if (obtainedPolarity == document.getPolarity()) {
				caseSolution.add(lexicon);
			}
		}
		return caseSolution;
	}

	private boolean predictDocumentPolarity(
			LexiconBasedClassifier classifier, Document document,
			Lexicon lexicon) {
		double score = classifier.getDocumentScore(document, lexicon);
//			score1= score;
		return classifier.predictPolarity(score);
	}

	private Case buildCase(Document document, CaseSolution caseSolution) {
		String name = document.getName();
		CaseDescription caseDescription = new CaseDescription(document);
		return new Case(name, caseDescription, caseSolution);
	}

	// private CaseDescription computeCaseDescription(Document document) {
	// return new CaseDescription(document);
	// }

}
