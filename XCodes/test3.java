import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class test3 {
	
	private static PrintWriter pw;
	private static MaxentTagger taggerModel;
	
	@SuppressWarnings("unused")
	private void training () throws IOException, Exception  {
		taggerModel = new MaxentTagger("TaggerModels/english-bidirectional-distsim.tagger");
		pw = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"), true);

		List<Class<? extends Lexicon>> sentimentLexicons = new ArrayList<Class<? extends Lexicon>>();
		addSentimentalLexiconsToLexiconsList(sentimentLexicons);

		List<CaseBase> caseBaseSet = new ArrayList<CaseBase>();

		String trainingSetPath = "C:/Master_Project/Programming/Java_Project/TrainingSet";
		//** Movie : positive reviews
		String path = trainingSetPath + "/Movie/Positive";
		List<Document> trainingDocumentsSet = DirectoryUtil
				.getListOfDocument(path);
		caseBaseSet.addAll(PopulationTheCaseBase.buildCaseBaseSet(trainingDocumentsSet, sentimentLexicons, taggerModel , true));
		
		//** Movie : negative reviews
		path = trainingSetPath + "/Movie/Negative";
		trainingDocumentsSet.clear();
		trainingDocumentsSet = DirectoryUtil.getListOfDocument(path);
		caseBaseSet.addAll(PopulationTheCaseBase.buildCaseBaseSet(trainingDocumentsSet, sentimentLexicons, taggerModel, false));
		
		List<CaseBase> normalizedCaseBaseSet = Normalize.applyMinMaxNormalization(caseBaseSet);
		Serialization.serialize(normalizedCaseBaseSet, "CaseBase_1.ser");
		pw.println("");
	}
	
	private static void addSentimentalLexiconsToLexiconsList(
			List<Class<? extends Lexicon>> sentimentLexicons) {
		sentimentLexicons.add(SentiWordNet.class);
		sentimentLexicons.add(MSOL.class);
	}
}

