package shared;

/**
 * Last modification 4 March 2013
 */

import java.io.Serializable;
import java.util.Arrays;

public class Case_March2013 implements Serializable {

	private static final long serialVersionUID = 5716931222511036926L;
	private String name;
	private CaseDescription caseDescription;
	private CaseSolution caseSolution;

	public Case_March2013() {
	}

	public Case_March2013(String name, CaseDescription caseDescription,
			CaseSolution caseSolution) {
		this.name = name;
		this.caseDescription = caseDescription;
		this.caseSolution = caseSolution;
	}

	public Case_March2013(CaseDescription caseDescription, CaseSolution caseSolution) {
		this.caseDescription = caseDescription;
		this.caseSolution.add(caseSolution);
	}

	public double[] getFeatures() { // is it OK?
		return caseDescription.getFeatures();
	}

	public CaseDescription getCaseDescription() {
		return caseDescription;
	}

	public void seCaseDescription(CaseDescription caseDescription) {
		this.caseDescription = caseDescription;
	}

	public CaseSolution getCaseSolution() {
		return caseSolution;
	}

	public void setCaseSolution(CaseSolution caseSolution) {
		this.caseSolution.add(caseSolution);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(name);
		stringBuilder.append(",");
		stringBuilder.append(Arrays.toString(caseDescription.getFeatures()));
		stringBuilder.append(",");
		stringBuilder.append(caseSolution.getLexicons());
		return stringBuilder.toString();
	}
}
