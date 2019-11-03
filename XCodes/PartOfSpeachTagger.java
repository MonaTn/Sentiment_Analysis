import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

public class PartOfSpeachTagger {
	
	public static DocumentPreprocessor applyPOSTagger(Document document)
			throws ClassNotFoundException, IOException {

		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String absoluteDocName = document.getPath()+"/"+document.getName(); 
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new FileInputStream(absoluteDocName), "utf-8"));
		
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}
}

 
