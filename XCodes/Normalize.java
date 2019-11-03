package testing;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.Constant;

public class Normalize {

	public double[] minValues;
	public double[] maxValues;
	
	public double[] getMinValues() {
		return minValues;
	}

	public void setMinValues(double[] minValues) {
		this.minValues = minValues;
	}

	public double[] getMaxValues() {
		return maxValues;
	}

	public void setMaxValues(double[] maxValues) {
		this.maxValues = maxValues;
	}

	public int arraySize = Constant.getFeatureSize();
	
	private void initializeNormalizer (Case oneCase) {
		double[] initialValues = oneCase.getCaseDescription().getFeatures();
		setMaxValues(initialValues);
		setMinValues(initialValues);
	}

	public List<double[]> exteractMinMaxValues(CaseBase caseBaseList) {
		Case firstCase = caseBaseList.getCases().get(0);
		initializeNormalizer(firstCase);
		for (Case oneCase : caseBaseList.getCases()) {
			rebuildMinMaxValue(oneCase.getCaseDescription().getFeatures());
		}
		List<double[]> minmaxList = new ArrayList<double[]>();
		minmaxList.add(minValues);
		minmaxList.add(maxValues);
		return minmaxList;
	}

	private void rebuildMinMaxValue(double[] features) {
		for (int i = 0; i < features.length; i++) {
			minValues[i] = Math.min(minValues[i], features[i]);
			maxValues[i] = Math.max(maxValues[i], features[i]);
		}
	}

	public CaseBase normalizeCaseBase(CaseBase caseBase, double[] minValues,
			double[] maxValues) throws IOException {
		CaseBase normalizedCaseBaseList = new CaseBase();
		for (Case oneCase : caseBase.getCases()) {
			CaseDescription normalizedCaseDescription = minMaxNormalization(
					oneCase.getCaseDescription().getFeatures(), minValues,
					maxValues);
			normalizedCaseBaseList.add(new Case(oneCase.getName(),
					normalizedCaseDescription, oneCase.getCaseSolution())); // ch
																			// name
		}
		return normalizedCaseBaseList;
	}

	public CaseDescription minMaxNormalization(double[] features,
			double[] minValues, double[] maxValues) {
		double[] normalizedFeatures = new double[arraySize];
		for (int i = 0; i < arraySize; i++) {
			double normalizedNumber = Math.abs((features[i] - minValues[i])
					* 10 / (maxValues[i] - minValues[i]));
			// TODO -1 is good idea? Is it good idea to use #.## ?
			// normalizedFeatures[i] = (Double.isNaN(normalizedNumber) ||
			// Double.isInfinite(normalizedNumber)) ? -1 :
			// roundTwoDecimals(normalizedNumber);
			normalizedFeatures[i] = (Double.isNaN(normalizedNumber) || Double
					.isInfinite(normalizedNumber)) ? -1 : normalizedNumber;
		}
		return new CaseDescription(normalizedFeatures);
	}

	@SuppressWarnings("unused")
	private Double roundTwoDecimals(double score) {
		return Double.valueOf(new DecimalFormat("#.##").format(score));
	}

}
