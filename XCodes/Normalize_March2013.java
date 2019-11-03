package testing;

/**
 * Last modification 4 March 2013
 */

import java.text.DecimalFormat;

public class Normalize_March2013 {

	public double[] normalizeArray(double[] array, double[] minValues,
			double[] maxValues, int lowerRange, int upperRange) { //  method as parameter 
		int arrayLength = array.length;
		double[] normalizedFeatures = new double[arrayLength];
		for (int i = 0; i < arrayLength; i++) {
			normalizedFeatures[i] = minMaxNormalization(array[i], minValues[i],
					maxValues[i], lowerRange, upperRange);
		}
		return normalizedFeatures;
	}

	public double minMaxNormalization(double digit, double min, double max,
			int lowerRange, int upperRange) {
		double normalizedDigit = (((digit - min) / (max - min)) * (upperRange - lowerRange))
				+ lowerRange;
		return (isValidNumber(normalizedDigit)) ? Math.abs(normalizedDigit)
				: -1;
	}

	private boolean isValidNumber(double digit) {
		return (!Double.isNaN(digit) || !Double.isInfinite(digit));
	}

	@SuppressWarnings("unused")
	private Double roundTwoDecimals(double score) {
		return Double.valueOf(new DecimalFormat("#.##").format(score));
	}
}
