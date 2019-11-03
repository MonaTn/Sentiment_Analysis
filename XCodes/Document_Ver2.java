import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

public class Document_Ver2 {
	
	private String name;
	private String path;
	private boolean polarity;
	private double score; //?
	private CaseDescription caseDescription;
	private DocumentPreprocessor documentPreprocessor; //?
	boolean discarde ; //**
	
	public Document_Ver2 (String name , String path, boolean polarity ) throws ClassNotFoundException, IOException {
		this.name = name;
		this.path = path;
		this.polarity = polarity;
		buildDocumentPreprocessor ();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getPolarity() {
		return polarity;
	}
	public void setPolarity(boolean polarity) {
		this.polarity = polarity;
	}
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
	public CaseDescription getCaseDescription() {
		return caseDescription;
	}
	public void setCaseDescription(CaseDescription caseDescription) {
		this.caseDescription = caseDescription;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String docpath) {
		this.path = docpath;
	}
	
	private void buildDocumentPreprocessor () throws ClassNotFoundException, IOException { //?
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String absoluteDocumentName = path + "/" + name;
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(absoluteDocumentName), "utf-8"));
		documentPreprocessor = new DocumentPreprocessor(
				bufferReader);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
	}
	
	public DocumentPreprocessor getDocumentPreprocessor() {
		return documentPreprocessor;
	}
}
