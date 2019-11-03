package training;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lexicon.Lexicon;
import lexicon.MSOL;
import lexicon.SentiWordNet;

import shared.CaseBase;
import shared.DirectoryUtil;
import shared.Document;
import shared.Serialization;
import testing.Normalize;


import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class TrainingPhase {
	
	private static List<Class<? extends Lexicon>> sentimentLexiconsList = new ArrayList<Class<? extends Lexicon>>();
	private static List<CaseBase> caseBaseList = new ArrayList<CaseBase>();
	private final static String baseTrainingSetPath = "C:/Master_Project/Programming/Java_Project/TrainingSet";
	private final static String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";
	private static       MaxentTagger taggerModel;

	public static void main(String[] args) throws IOException, Exception  {
		 
		taggerModel = new MaxentTagger(englishModel);
		populateSentimentLexiconList();

		//** Movie : positive reviews
		caseBaseList.addAll(populateCaseBaseList("/Movie/5Positive", true));
		
		//** Movie : negative reviews
		caseBaseList.addAll(populateCaseBaseList("/Movie/5Negative", false));
		
		List<CaseBase> normalizedCaseBaseSet = Normalize.normalizedCaseBaseList(caseBaseList);
		Serialization.serialize(normalizedCaseBaseSet, "CaseBase_Movie_04112012_6.ser");//ch
		printCaseBaseSetToFile(normalizedCaseBaseSet);
	}
	
	private static void populateSentimentLexiconList() {
		sentimentLexiconsList.add(SentiWordNet.class);
		sentimentLexiconsList.add(MSOL.class);
	}
	
	private static List<CaseBase> populateCaseBaseList(String subTrainingSetPath, boolean truePolarity) throws IOException, Exception {
		String path = baseTrainingSetPath + subTrainingSetPath;
		List<Document> trainingDocumentsList = DirectoryUtil.getListOfDocument(path, truePolarity);
		 return PopulateCaseBaseList.buildCaseBaseList(trainingDocumentsList, sentimentLexiconsList, taggerModel);
	}
	
	private static void printCaseBaseSetToFile (List<CaseBase> caseBasedSet) throws IOException { 
		File resultFileOfDocuments = new File ("Result/CaseBaseSet_04112012_6.csv");//ch
		if (! resultFileOfDocuments.exists()) {
			resultFileOfDocuments.createNewFile();
		}
		BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(resultFileOfDocuments));
		for (CaseBase caseBase : caseBasedSet){
			bufferWrite.write(caseBase.getCaseName()+" ,"+ Arrays.toString(caseBase.getCaseDescription().getFeatures())+" , "+caseBase.getCaseSolution().toString());
			bufferWrite.newLine();
		}
		bufferWrite.close();
	}
}


