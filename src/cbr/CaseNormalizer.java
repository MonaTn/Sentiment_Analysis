package cbr;

import basic.Constant;


public class CaseNormalizer {
	private final static int length = Constant.featureSize;

	public static Case caseNormalize(Case oneCase, double[] minValues,
			double[] maxValues) {
		double[] features = oneCase.getCaseDescription().getFeatures();// remove
																		// method
																		// getFeatures
		double[] normalizedFeature = new double[length];
		for (int i = 0; i < length; i++) {
			normalizedFeature[i] = minMaxNormalize(features[i],
					minValues[i], maxValues[i]);
		}
		CaseDescription normalizedCaseDescription = new CaseDescription(
				normalizedFeature);
		return new Case(oneCase.getName(), normalizedCaseDescription,
				oneCase.getCaseSolution());
	}

	public static CaseBase caseBaseNormalize(CaseBase caseBase, double[] minValues , double[] maxValues) {
		CaseBase normalizedCaseBase = new CaseBase();
		for (Case oneCase : caseBase.getCases()) {
			System.out.println(oneCase.toString());
			Case normalizedCase = caseNormalize(oneCase, minValues, maxValues);
			System.out.println(normalizedCase.toString());
			normalizedCaseBase.add(normalizedCase);
		}
		return normalizedCaseBase;
	}

	public static CaseDescription caseDescriptionNormalize(
			CaseDescription caseDescription, double[] minValues,
			double[] maxValues) {
		double[] features = caseDescription.getFeatures();
		double[] normalizedFeatures = new double[length];
		for (int i = 0; i < length; i++) {
			normalizedFeatures[i] = minMaxNormalize(features[i],
					minValues[i], maxValues[i]);
		}
		return new CaseDescription(normalizedFeatures);
	}

	public static double minMaxNormalize(double digit, double min,
			double max) {
		final int lowerRange = 0;
		final int upperRange = 1;
		double normalizedDigit = (((digit - min) / (max - min)) * (upperRange - lowerRange))
				+ lowerRange;
		return (isValidNumber(normalizedDigit)) ? Math.abs(normalizedDigit)
				: 0;
	}

	private static boolean isValidNumber(double digit) {
		return (!Double.isNaN(digit) && !Double.isInfinite(digit));
	}
}