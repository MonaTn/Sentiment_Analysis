package train;

/**
 * Last modification 25 February 2013
 */

import java.io.IOException;

public class CaseBaseTrain {

	private static String resultPath = "../../Results/2014/Train/Jan/";
	private static String rootTrainingSetPath = "../../DataSet/";
	private static String serializedCaeBaseFileName = "All-BUT-Music(20140123).ser";
	private static String csvCaseBaseFileName = "All-BUT-Music(20140123).csv";

	public static void main(String[] args) throws IOException, Exception {
		CaseBaseTrain cbrTrainer = new CaseBaseTrain();
		CaseBaseBuilder caseBaseBuilder = new CaseBaseBuilder();

		cbrTrainer.train();
	}

	private CaseBaseBuilder initialize(CaseBaseBuilder trainer) {
		trainer.setResultPath(resultPath);
		trainer.setRootOfTrainingPath(rootTrainingSetPath);
		return trainer;
	}

	public void train() throws IOException, Exception {
		CaseBaseBuilder trainer = new CaseBaseBuilder();
		setParameters(trainer);
		trainer.train();
	}

	private void setParameters(CaseBaseBuilder trainer) {
		trainer.setResultPath(resultPath);
		trainer.setRootOfTrainingPath(rootTrainingSetPath);
		trainer.setCsvCaseBaseFileName(csvCaseBaseFileName);
		trainer.setSerializedCaeBaseFileName(serializedCaeBaseFileName);
	}

}
