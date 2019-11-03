package cbr;

/**
 * Last modification 24 March 2014
 * - 29 March 2013
 **/

import java.io.Serializable;
import java.util.Arrays;

public class Case implements Serializable {

	private static final long serialVersionUID = 5716931222511036926L;

	private String name;
	private CaseDescription caseDescription;
	private CaseSolution caseSolution;

	public Case() {
	}

	public Case(String name, CaseDescription caseDescription,
			CaseSolution caseSolution) {
		this.name = name;
		this.caseDescription = caseDescription;
		this.caseSolution = caseSolution;
	}

	// public Case(CaseDescription caseDescription, CaseSolution caseSolution) {
	// this.caseDescription = caseDescription;
	// this.caseSolution.add(caseSolution);
	// }

	// remove method getFeatures 2014-03-24
	// public double[] getFeatures() {
	// return caseDescription.getFeatures();
	// }

	public CaseDescription getCaseDescription() {
		return caseDescription;
	}

	// public void seCaseDescription(CaseDescription caseDescription) {
	// this.caseDescription = caseDescription;
	// }

	public CaseSolution getCaseSolution() {
		return caseSolution;
	}

	// public void setCaseSolution(CaseSolution caseSolution) {
	// this.caseSolution.add(caseSolution);
	// }

	public String getName() {
		return name;
	}

	// public void setName(String name) {
	// this.name = name;
	// }

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getName());
		stringBuilder.append(Arrays.toString(caseDescription.getFeatures()));//
		stringBuilder.append(caseSolution.toString());
		return stringBuilder.toString();
	}

	public String toooString() {
		return name;

	}
}
