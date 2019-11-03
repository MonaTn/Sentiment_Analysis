package shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lexicon.Lexicon;

public class Case implements Serializable {

	private static final long serialVersionUID = 5716931222511036926L;
	private String caseName;
	private CaseDescription caseDescription;
	private List<Class<? extends Lexicon>> caseSolution = new ArrayList<Class<? extends Lexicon>>();

	public Case () {}

	public Case (String caseName, CaseDescription caseDescription,
			List<Class<? extends Lexicon>> caseSolution) {
		this.caseName = caseName;
		this.caseDescription = caseDescription;
		this.caseSolution.addAll(caseSolution);
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public CaseDescription getCaseDescription() {
		return caseDescription;
	}

	public void seCaseDescription(CaseDescription caseDescription) {
		this.caseDescription = caseDescription;
	}

	public List<Class<? extends Lexicon>> getCaseSolution() {
		return caseSolution;
	}

	public void setCaseSolution(List<Class<? extends Lexicon>> caseSolution) {
		this.caseSolution.addAll(caseSolution);
	}
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");
		stringBuilder.append(caseName);
		stringBuilder.append(caseSolution);
		stringBuilder.append(NEW_LINE);
		stringBuilder.append(Arrays.toString(caseDescription.getFeatures()));
		return stringBuilder.toString();
	}
}
