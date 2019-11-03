package test;

import java.io.Serializable;
import java.util.List;

import cbr.CaseBase;
import lexicon.Lexicon;

public class CaseBaseLexiconList implements Serializable {

	private static final long serialVersionUID = 6865560011984393364L;
	// private static final CaseBaseLexiconList INSTANCE = new
	// CaseBaseLexiconList();

	CaseBase casebase;
	List<Lexicon> lexicons;

	public CaseBase getCasebase() {
		return casebase;
	}

	public void setCasebase(CaseBase casebase) {
		this.casebase = casebase;
	}

	public List<Lexicon> getLexicons() {
		return lexicons;
	}

	// public CaseBaseLexiconList getInstance() {
	// return INSTANCE;
	// }

	public void setLexicons(List<Lexicon> lexicons) {
		this.lexicons = lexicons;
	}

	// private Object readResolve() throws ObjectStreamException {
	// return INSTANCE;
	// }

}
