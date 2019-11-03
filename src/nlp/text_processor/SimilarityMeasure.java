package nlp.text_processor;

/**
 * Last modification 4 March 2013
 */

public class SimilarityMeasure {

	public static double euclideanDistance(double[] array1, double[] array2) {
		double sum = 0;
		int length = array1.length;
		for (int i = 0; i < length; i++) {
			sum += Math.pow(array1[i] - array2[i], 2);
		}
		return Math.sqrt(sum);
	}

}
