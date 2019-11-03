package Utility;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Main.CaseBase;
import Main.CaseDescription;


public class CopyOfNormalize {

	private static double[] minValues = new double[20];
	private static double[] maxValues = new double[20];
	private static PrintWriter printWriter;
	
	public static List<CaseBase> applyMinMaxNormalization (List<CaseBase> caseBaseList) throws IOException {
		initializeArray(minValues, Integer.MAX_VALUE);
		initializeArray(maxValues, Integer.MIN_VALUE);
		printWriter =  new PrintWriter(new OutputStreamWriter(System.out, "utf-8"), true);

		for (CaseBase caseBase : caseBaseList) {
			double[] features = caseBase.getCaseDescription().getFeatures();
			findMinMaxValues(features); // find min and max values of each feature of CaseDescription
		}
		List<double[]> minmaxArrays = new ArrayList<double[]>();//?
		minmaxArrays.add(minValues);
		minmaxArrays.add(maxValues);
		Serialization.serialize(minmaxArrays, "minmax.ser");
		printArrays () ;
		
		List<CaseBase> normalizedCaseBaseList = new ArrayList<CaseBase>();
		for (CaseBase caseBase : caseBaseList) {
			double[] normalizedFeatures = min_maxNormalization (caseBase.getCaseDescription().getFeatures());
			CaseDescription normalizedCaseDescription = new CaseDescription(); //?
			normalizedCaseDescription.setFeatures(normalizedFeatures);
			normalizedCaseBaseList.add(new CaseBase(caseBase.getCaseName(), normalizedCaseDescription, caseBase.getCaseSolution()));
		}
		printCaseBaseSet (normalizedCaseBaseList);
		return normalizedCaseBaseList;
	}
	
	private static void initializeArray (double[] array , int amount) {
		Arrays.fill(array, (double) amount);
	}
	
	private static void findMinMaxValues (double [] features) {  //?
		for (int i = 0 ; i<20 ; i++) {
			minValues[i] = Math.min(minValues[i], features[i]);
			maxValues[i] = Math.max(maxValues[i], features[i]);
		}
	}
	
	private static  double[]  min_maxNormalization (double[] features) {
		double[] normalizedFeatures = new double[20];
		for (int i =0 ; i<20 ; i++) {
			double normalizedNumber = (features[i] - minValues[i]) * 10 / (maxValues[i] - minValues[i]);
			normalizedFeatures[i] =  (Double.isNaN(normalizedNumber) || Double.isInfinite(normalizedNumber)) ? -1 : roundTwoDecimals(normalizedNumber);
		}
		return normalizedFeatures;
	}
	
	private static  Double roundTwoDecimals(double score) {
		return Double.valueOf(new DecimalFormat("#.##").format(score));
	}

	private static void printCaseBaseSet (List<CaseBase> caseBaseSet) throws IOException { //chang
		for (CaseBase caseBase : caseBaseSet) {
			caseBase.print();
		}
	}
	
	private static void printArrays () {
		printWriter.println("********************************************************");
		printWriter.println(Arrays.toString(minValues));
		printWriter.println(Arrays.toString(maxValues));
		printWriter.println("********************************************************");
	}
}
