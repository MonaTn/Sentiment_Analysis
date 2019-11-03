package Old_Test;

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
