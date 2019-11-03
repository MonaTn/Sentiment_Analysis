import java.io.IOException;
//import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class test1 {

	private static double[] minValues = new double[20];
	private static double[] maxValues = new double[20];
	
	public static void main(String[] args) throws IOException {
		initializeArray(minValues, 10000);
		initializeArray(maxValues, 0);
		
		List<CaseBase> caseBaseSet = new ArrayList<CaseBase>();
		caseBaseSet = Serialization.deSerialize("CaseBaseSet1.ser");
		for (CaseBase caseBase : caseBaseSet) {
			double[] features = caseBase.getCasedescription().getFeatures();
			System.out.println(Arrays.toString(features));
			findMinMaxValues(features);
		}
		System.out.println("********************************************************");
		System.out.println(Arrays.toString(minValues));
		System.out.println(Arrays.toString(maxValues));
		System.out.println("********************************************************");
		List<CaseBase> normalizedCaseBaseSet = new ArrayList<CaseBase>();
		for (CaseBase caseBase : caseBaseSet) {
			double[] normalizedFeatures = min_maxNormalization (caseBase.getCasedescription().getFeatures());
			CaseDescription nCaseDescription = new CaseDescription();
			nCaseDescription.setFeatures(normalizedFeatures);
			normalizedCaseBaseSet.add(new CaseBase(caseBase.getCaseName(), nCaseDescription, caseBase.getCaseSolution()));
			System.out.println(Arrays.toString(normalizedFeatures));
		}
		System.out.println("********************************************************");
		printCaseBaseSet (normalizedCaseBaseSet);
		Serialization.serialize(normalizedCaseBaseSet, "NormalCaseBaseSet1.ser");
	}
	
	private static void initializeArray (double[] array , int amount) {
		Arrays.fill(array, (double) amount);
	}
	
	private static void findMinMaxValues (double [] features) {
		for (int i = 0 ; i<20 ; i++) {
			minValues[i] = Math.min(minValues[i], features[i]);
			maxValues[i] = Math.max(maxValues[i], features[i]);
		}
	}
	
	private static  double[]  min_maxNormalization (double[] features) {
		double[] normalizedFeatures = new double[20];
		for (int i =0 ; i<20 ; i++) {
			normalizedFeatures[i] = (features[i] - minValues[i]) * 10 / (maxValues[i] - minValues[i]); 
		}
		return normalizedFeatures;
	}
	
//	private  Double roundTwoDecimals(double score) {
//		return (!isInfinity(score)) ? Double.valueOf(new DecimalFormat("#.##").format(score)) : -1;
//	}
//	private static boolean isInfinity(double numer){
//		return Double.isInfinite(numer);
//	}
	private static void printCaseBaseSet (List<CaseBase> caseBaseSet) {
		for (CaseBase cb : caseBaseSet) {
			System.out.println (cb.getCaseName() +" , "+ cb.getCaseSolution().toString());
			System.out.println (Arrays.toString(cb.getCasedescription().getFeatures()));
		}
	}
}
