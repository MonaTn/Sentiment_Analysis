import java.util.Arrays;


public class Normalization {
	
	private double[] minValues = new double[20];
	private double[] maxValues = new double[20];
	
	public double[] getMinValues() {
		return minValues;
	}
	public void setMinValues(double[] minValues) {
		this.minValues = minValues;
	}
	public static void initializeArray (double[] array , int amount) {
		Arrays.fill(array, (double) amount);
	}
	
	public double[] getMaxValues() {
		return maxValues;
	}
	public void setMaxValues(double[] maxValues) {
		this.maxValues = maxValues;
	}
	
	public  double[]  min_maxNormalization (double[] features) {
		double[] normalizedFeatures = new double[20];
		for (int i =0 ; i<20 ; i++) {
			normalizedFeatures[i] = (features[i] - minValues[i]) * 10 / (maxValues[i] - minValues[i]); 
		}
		return normalizedFeatures;
	}

	public  void findMinMaxValues (double [] features) {
		for (int i = 0 ; i<20 ; i++) {
			minValues[i] = Math.min(minValues[i], features[i]);
			maxValues[i] = Math.max(maxValues[i], features[i]);
		}
	}
}
