package shared;


/**
 * must refactore 
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility2 {

    private final static int length =  Constant.featureSize;
    
    public static void initializeArray(double[] targetArray, double[] sourceArray) {
        targetArray = Arrays.copyOf(sourceArray, length);
    }

    public static List<double[]> findMinMaxValues (double[] array1, double[] array2) {
        double[] minArray = new double[length];
        double[] maxArray = new double[length];
        List<double[]> minMax = new ArrayList<double[]>();
        for (int i = 0; i < length; i++) {
            minArray[i] = Math.min(array1[i], array2[i]);
            maxArray[i] = Math.max(array1[i], array2[i]);
        }
        minMax.add(minArray);
        minMax.add(maxArray);
        return minMax;
    }
    
    public static double[] findMin (double[] array1, double[] array2){
    	double[] min = new double[length]; 
    	for (int i = 0 ; i < length ; i ++) {
    		min[i] = Math.min(array1[i], array2[i]);
    	}
    	return min;
    }
    
    public static double[] findMax (double[] array1, double[] array2){
    	double[] max = new double[length]; 
      	for (int i = 0 ; i < length ; i ++) {
    		max[i] = Math.max(array1[i], array2[i]);
    	}
    	return max;
    }
}
