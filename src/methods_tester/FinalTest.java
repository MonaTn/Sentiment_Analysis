package methods_tester;

import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class FinalTest {
	static List<String> patternsList;
	static TregexPatternCompiler macros;
	static final String fileName = "Music12055.text";
	static final String path = "../../TrainingSet/Test";

	public static void main(String[] args) throws IOException, Exception {
		// NegationPreprocessor negationProcessor = new NegationPreprocessor();
		// patternsList = negationProcessor.getPatternsList();
		// macros = negationProcessor.getMacros();
		// Document document = new Document(fileName, path, false);
		// NegationPreprocessor_MinMinStrategy nProcessor = new
		// NegationPreprocessor_MinMinStrategy();
		// nProcessor.extractWordsListOfDocument(document);
	}
}

// private List<TaggedWord> getTaggedwords(List<Tree> trees) {
// List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
// for (Tree tree : trees) {
// taggedWords.addAll(tree.taggedYield());
// }
// editingMisspellingWords(taggedWords);
// return taggedWords;
// }

// private List<TaggedWord> editingMisspellingWords(
// List<TaggedWord> taggedWords) { // dic
// for (TaggedWord taggedWord : taggedWords) {
// String word = taggedWord.word().toLowerCase();
// String tag = taggedWord.tag();
// if (word.equals("~ca") && tag.equals("MD")) {
// taggedWord.setWord("~can");
// } else if (word.equals("~wo") && tag.equals("MD")) {
// taggedWord.setWord("~will");
// }
// }
// return taggedWords;
// }