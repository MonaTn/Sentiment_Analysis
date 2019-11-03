package cbr;

/**
 *  Last modification 24 Jan 2014
 */

import java.io.Serializable;
import java.util.Arrays;

import basic.Constant;

public class CaseDescription implements Serializable {
	private static final long serialVersionUID = -9139701688241540085L;

	private double[] featuresList;

	public CaseDescription(double[] features) {
		featuresList = new double[Constant.featureSize];
		featuresList = Arrays.copyOf(features, features.length);
	}

	public double[] getFeatures() {
		return featuresList;
	}

}
