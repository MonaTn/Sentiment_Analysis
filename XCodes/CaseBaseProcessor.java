package testing;

import java.io.IOException;

import shared.Case;
import shared.CaseBase;
import shared.CaseDescription;
import shared.CaseSolution;
import shared.Constant;
import shared.Serialization;

public class CaseBaseProcessor {

	private double[] minValues;
	private double[] maxValues;
	private final int length = Constant.getFeatureSize();
	
	public CaseBase extractCaseBase(String fileName) throws IOException {
		return (CaseBase) Serialization.deSerialize(fileName);
	}
	public void saveCaseBase (String fileName) {
		
	}
	public CaseBase normalizedCaseBase(CaseBase caseBase,
			NormalizationMethod normalizer) {
		CaseBase normalizedCaseBase = new CaseBase();
		for (Case oneCase : caseBase.getCases()) {
			Case normalizedCase = normalizingCase(oneCase, normalizer);
			normalizedCaseBase.add(normalizedCase);
		}
		return normalizedCaseBase;
	}

	private Case normalizingCase(Case oneCase, NormalizationMethod normalizer) {
		double[] features = oneCase.getFeatures();
		double[] normalizedfeature = new double[length];
		for (int i = 0; i < length; i++) {
			normalizedfeature[i] = normalizer.minMaxNormalization(features[i],
					minValues[i], maxValues[i]);
		}
		String name = oneCase.getName();
		CaseDescription caseDescription = new CaseDescription(normalizedfeature);
		CaseSolution caseSolution = oneCase.getCaseSolution();
		return new Case(name, caseDescription, caseSolution);
	}

	public void setMinValues(CaseBase caseBase) {
		this.minValues = caseBase.extractMinValues();
	}

	public void setMaxValues(CaseBase caseBase) {
		this.maxValues = caseBase.extractMaxValues();
	}

	public double[] getMinValues() {
		return minValues;
	}

	public double[] getMaxValues() {
		return maxValues;
	}

}
