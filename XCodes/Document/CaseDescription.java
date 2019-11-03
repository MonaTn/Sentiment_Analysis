package shared;

/**
 * Last modification 10 March 2013
 */

import java.io.Serializable;
import java.util.Arrays;

public class CaseDescription implements Serializable {
	private static final long serialVersionUID = -9139701688241540085L;

	private double[] features;

	public CaseDescription(double[] features) {
		this.features = new double[17];
		this.features = Arrays.copyOf(features, features.length);
	}

	public void setFeatures(double[] features) {
		this.features = Arrays.copyOf(features, features.length);
	}

	public double[] getFeatures() {
		return features;
	}

}
