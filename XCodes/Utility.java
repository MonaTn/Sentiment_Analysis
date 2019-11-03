package shared;


/**
 * must refactore 
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility {

    private final static int length = Constant.featureSize;

    public static double[] findMinimumValues(double[] array1, double[] array2) {
        double[] minArray = new double[length];
        for (int i = 0; i < length; i++) {
            minArray[i] = minimum(array1[i], array2[i]);
        }
        return minArray;
    }

    private static double minimum(double digit1, double digit2) {
        return Math.min(digit1, digit2);
    }

    public static double[] findMaximumValues(double[] array1, double[] array2) {
        double[] maxArray = new double[length];
        for (int i = 0; i < length; i++) {
            maxArray[i] = maximum(array1[i], array2[i]);
        }
        return maxArray;
    }

    private static double maximum(double digit1, double digit2) {
        return Math.max(digit1, digit2);
    }

    public static void initializeArray(double[] sourceArray, double[] targetArray) {
        targetArray = Arrays.copyOf(sourceArray, length);
    }

    public static List<double[]> findMinMaxValues (double[] array1, double[] array2) {
        double[] minArray = new double[length];
        double[] maxArray = new double[length];
        List<double[]> minMax = new ArrayList<double[]>();
        for (int i = 0; i < length; i++) {
            minArray[i] = minimum(array1[i], array2[i]);
            maxArray[i] = maximum(array1[i], array2[i]);
        }
        minMax.add(minArray);
        minMax.add(maxArray);
        return minMax;
    }
}
