package cbr;

import java.util.ArrayList;
import java.util.List;

import lexicon.GeneralInquirer;
import lexicon.Lexicon;
import lexicon.MSOL;
import lexicon.NRC;
import lexicon.SentiWordNet;
import lexicon.SubjectivityClues;
import nlp.TextProcessor;
import nlp.TextProcessorFactory;

public class CBRTrainer {

	private final String[] domains = { "Apparel", "Electronics" }; // ,
																	// "Electronics",
															// "Hotel", "Movie",
															// "Music" };
	private String serializedCaeBaseFileName;
	private String csvCaseBaseFileName;
	private List<Lexicon> lexiconsList;
	private CaseAuthoring caseAuthore;
	private TextProcessor processor;

	public CBRTrainer() {
		caseAuthore = new CaseAuthoring();
		createLexiconList();
	}

	public void setStemmer(String stemmerName) { // ? name
		TextProcessorFactory factory = new TextProcessorFactory();
		factory.setStemmer(stemmerName);
		processor = factory.create();
	}

	public void setNegationFlag(boolean flag) {
		processor.enableNegationAnalysis(flag);
	}

	public void train(CaseListBuilder caseBaseBuilder) throws Throwable {
		for (int i = 0; i < domains.length; i++) {
			buildCaseBase(caseBaseBuilder, domains[i], processor);
		}
	}

	private void buildCaseBase(CaseListBuilder casesBuilder, String holdOutDomain,
			TextProcessor processor)
			throws Throwable {
		CaseBase caseBase = new CaseBase();
		for (String domainName : domains) {
			if (!domainName.equals(holdOutDomain)) {
				System.out.println("\n Domain Name is : " + domainName);
				setCaseBaseSavedFileNames(domainName);
				List<Case> cases = casesBuilder.build(domainName, lexiconsList,
						caseAuthore, processor);
				caseBase.add(cases);
			}
		}
		casesBuilder.save(caseBase, serializedCaeBaseFileName,
				csvCaseBaseFileName);
	}

	private void setCaseBaseSavedFileNames(String domainName) {
		serializedCaeBaseFileName = domainName + ".ser";
		csvCaseBaseFileName = domainName + ".csv";
	}

	private void createLexiconList() {
		lexiconsList = new ArrayList<Lexicon>();
		lexiconsList.add(new SentiWordNet());
		lexiconsList.add(new MSOL());
		lexiconsList.add(new GeneralInquirer());
		lexiconsList.add(new SubjectivityClues());
		lexiconsList.add(new NRC());
	}
}
