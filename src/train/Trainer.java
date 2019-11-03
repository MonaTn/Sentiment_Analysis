package train;

import cbr.CBRTrainer;
import cbr.CaseListBuilder;
import classifier.LexiconBasedClassifier;




public class Trainer {

	private static String caseBasePath = "../../Results/2015/";
	private static String rootTrainingSetPath = "../../DataSet/Small Train/";
	private static final String stemmerName = "SnowBall_Stemmer";
	private static final boolean negationFlag = false;

	public static void main(String[] args) throws Throwable {
		CBRTrainer cbrTrainer = new CBRTrainer();
		cbrTrainer.setStemmer(stemmerName);
		cbrTrainer.setNegationFlag(negationFlag);

		CaseListBuilder casesBuilder = new CaseListBuilder();
		casesBuilder.setClassifier(new LexiconBasedClassifier());
		casesBuilder.setPaths(caseBasePath, rootTrainingSetPath);

		cbrTrainer.train(casesBuilder);
	}

}
