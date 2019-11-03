package testing;

public class NormalizationMethod {

	public double minMaxNormalization(double digit, double min, double max) {
		final int lowerRange = 0;
		final int upperRange = 10;
		double normalizedDigit = (((digit - min) / (max - min)) * (upperRange - lowerRange))
				+ lowerRange;
		return (isValidNumber(normalizedDigit)) ? Math.abs(normalizedDigit)
				: -1;
	}

	private boolean isValidNumber(double digit) {
		return (!Double.isNaN(digit) || !Double.isInfinite(digit));
	}

}
