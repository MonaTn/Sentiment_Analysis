package training;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lexicon.Lexicon;


public class CaseBase implements Serializable{
	
	private static final long serialVersionUID = 5716931222511036926L;
	private String caseName;
	private CaseDescription caseDescription ;
	private List<Class<? extends Lexicon>>  caseSolution = new ArrayList<Class<? extends Lexicon>>();
	private double euclideanDistance;

	public CaseBase () {}

	public CaseBase (String caseName , CaseDescription caseDescription, List<Class<? extends Lexicon>>  caseSolution) {
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
	
	public void print() throws IOException {
		System.out.printf(("\n CaseBase Name : %20s Case solution : %s Case Euclidean-distance : %f \n Case description : %s"), this.getCaseName(), this.getCaseSolution().toString(), this.getEucliDeandistance(),Arrays.toString(this.getCaseDescription().getFeatures()));
	}

	public double getEucliDeandistance() {
		return euclideanDistance;
	}

	public void setEuclideanDistance(double euclideanDistance) {
		this.euclideanDistance = euclideanDistance;
	}

}
