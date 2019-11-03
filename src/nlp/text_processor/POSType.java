package nlp.text_processor;

/**
 * Change : discrite POS name for Adjective, Adverb and Verb
 * @author bitor1
 *
 */

public enum POSType {

	CC("Conjunction"),
	CD("Number"),
	DT("Determiner"),
	EX("Existential"),
	FW("ForiengWord"),
	IN("Preposition"),
	JJ("Adjective"),
	JJR("Adjective_R"),
	JJS("Adjective_S"),
	LS("ListItemMarker"),
	MD("Modal"),
	NN("Noun"),
	NNP("Noun_P"),
	NNPS("Noun_PS"),
	NNS("Noun_S"),
	PDT("Predeterminer"),
	POS("PossessiveEnding"),
	PRP("Pronoun"),
	PRP$("Pronoun"),
	RB("Adverb"),
	RBR("Adverb_R"),
	RBS("Adverb_S"),
	RP("Particle"),
	SYM("Symbol"),
	TO("To"),
	UH("Interjection"),
	VB("Verb"),
	VBD("Verb_D"),
	VBG("Verb_G"),
	VBN("Verb_N"),
	VBP("Verb_P"),
	VBZ("Verb_Z"),
	WDT("WH-Determiner"),
	WP("Wh-Pronoun"),
	WP$("PossessiveWh-Pronoun"),
	WRB("WH-Adverb"),
	PUNCT("Punctuation"),
	ROOT("Root"),
	PRN("Parenthese"),
	FRAG("Fragment"),
	S("Sub-Clause"),
	SBAR("SBAR");

	private String name;

	POSType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
