package xExtraClass;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class StringParser {

	public static void main(String[] args) {
		String grammar = args.length > 0 ? args[0]
				: "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
		String sentence = "I decided not to go to college!";
//		lp.parse(sentence).pennPrint();
		System.out.println(lp.parse(sentence).toString());
	}
}
