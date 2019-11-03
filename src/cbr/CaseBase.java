package cbr;

/**
 * Last modification 28 February 2013
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utilities.Utility;
import basic.Constant;

public class CaseBase implements Serializable {

	private static final long serialVersionUID = -2514867103449049009L;

	private List<Case> caseList;
	private final int length = Constant.featureSize;
	private double[] minValues = new double[length];
	private double[] maxValues = new double[length];

	public CaseBase() {
		caseList = new ArrayList<Case>();
	}

	public List<Case> getCases() {
		return caseList;
	}

	// public void setCaseBase(List<Case> cases) {
	// this.caseList = cases;
	// }

	public void add(Case oneCase) {
		caseList.add(oneCase);
	}

	public void add(List<Case> cases) {
		this.caseList.addAll(cases);
	}

	public double[] getMinValues() {
		minValues = caseList.get(0).getCaseDescription().getFeatures();
		for (Case oneCase : caseList) {
			double[] caseFeatures = oneCase.getCaseDescription().getFeatures();
			minValues = Utility.findMin(minValues, caseFeatures);
		}
		return minValues;
	}

	public double[] getMaxValues() {
		// remove method getFeatures
		maxValues = caseList.get(0).getCaseDescription().getFeatures();
		for (Case oneCase : caseList) {
			double[] caseFeatures = oneCase.getCaseDescription().getFeatures();
			maxValues = Utility.findMax(maxValues, caseFeatures);
		}
		return maxValues;
	}

}
