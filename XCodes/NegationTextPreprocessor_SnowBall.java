package xExtraClass;

/**
 * MinMinStrategy
 * last modification 16 October 2013
 * with SnowBall stemmer
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Negation.NegationAnalysis;
import Negation.NegationCues;
import Negation.TregexTree;

import shared.Document;
import shared.EnglishStemmer;
import shared.SnowBallStemmer;
import shared.Word;
import shared.WordList;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class NegationTextPreprocessor_SnowBall {

	private List<Tree> unnegatedTrees;
	private List<String> patterns;
	private List<Tree> treesToProcess;
	private List<Tree> subExtractedTrees;
	private SnowBallStemmer stemmer;
	private MaxentTagger taggerModel;
	private TregexPatternCompiler macros;
	private LexicalizedParser parser;
	private NegationAnalysis negationAnalyzer;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public NegationTextPreprocessor_SnowBall() throws Exception {
		initializeTagger();
		initializeLexicalizedParser();
		stemmer = new EnglishStemmer();
		negationAnalyzer = new NegationAnalysis();
		patterns = negationAnalyzer.getPatternsList();
		macros = negationAnalyzer.getMacros();
	}

	private void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeLexicalizedParser() {
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		parser = LexicalizedParser.loadModel(grammar, options);
	}

	private String stem(String word) {
		stemmer.setCurrent(word);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	public WordList extractWordList(Document document)
			throws IOException {
		List<WordList> sentences = getTaggedWordListsOfDocument(document);
		document.setTotalSentencesCount(sentences.size());
		return getListOfWordsWithCounts(sentences);
//		System.out.println(Arrays.toString(wordsList.getWords().toArray()));
//		System.out.println(wordsList.getWords().size());
//		return wordsList;
	}

	private List<WordList> getTaggedWordListsOfDocument(Document document)
			throws IOException {
		List<WordList> sentences = new ArrayList<WordList>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessor(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> taggedWordsOfSentence = getTaggedWords(sentence); // **
			WordList wordsList = convertTagedWordsToWordList(taggedWordsOfSentence);
			sentences.add(wordsList);
		}
		return sentences;
	}

	private DocumentPreprocessor documentPreprocessor(Document document)
			throws FileNotFoundException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String fileName = document.getAbsoluteFileName();
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				new FileReader(fileName));
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

	/* Negation detection part */
	private List<TaggedWord> getTaggedWords(List<HasWord> sentence) {
		List<TaggedWord> taggedWord = taggerModel.tagSentence(sentence);
		if (negationAnalyzer.containNegationCues(sentence)) {
			// System.out.println("___________________");
			// System.out.println("Negation happend ! ==> " + sentence);
			List<HasWord> negSentence = getTaggedWordsOfNegatedSentence(sentence);
			int i = 0 ;
			for (HasWord word : negSentence) {
				taggedWord.get(i).setWord(word.toString());
				i++;
			}
		}
//		else {
//			return taggerModel.tagSentence(sentence);
//		}
		return taggedWord;
	}

	private List<HasWord> getTaggedWordsOfNegatedSentence(
			List<HasWord> sentence) {
		unnegatedTrees = new ArrayList<Tree>();
		treesToProcess = new ArrayList<Tree>();
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		List<Tree> negatedTrees = new ArrayList<Tree>();
		Tree treeOfSentence = parser.parse(sentence);
		// System.out
		// .println("@@ Tree of sentence : " + treeOfSentence.toString());
		treesToProcess.add(treeOfSentence);
		while (treesToProcess.size() != 0) {
			for (Tree tree : treesToProcess) {
				// System.out.println("Tree to process : " + tree.toString());
				negatedTrees.addAll(exteractNegatedTrees(tree));
			}
			treesToProcess.clear();
			treesToProcess.addAll(subExtractedTrees);
			subExtractedTrees.clear();
		}
		if (negatedTrees.isEmpty()) {
			// System.out.println("-- Negated tree is null!");
			return sentence;
//			return taggerModel.tagSentence(sentence);
		} else {
			List<Tree> realNegatedTrees = removePseudoNegation(negatedTrees);
			addNegationSymbolToWords(realNegatedTrees);
			taggedWords.addAll(getTaggedwords(realNegatedTrees));
			taggedWords.addAll(getTaggedwords(unnegatedTrees));
			return sentence;
		}
	}

	private List<Tree> exteractNegatedTrees(Tree tree) {
		List<Tree> negatedTrees = new ArrayList<Tree>();
		subExtractedTrees = new ArrayList<Tree>(); // ?
		for (String pattern : patterns) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				TregexTree matchedTree = new TregexTree(matcher.getMatch());
				// System.out.println("**Negated Pattern : " + pattern);
				// System.out.println("** Negated Tree : "
				// + matchedTree.toString());
				subExtractedTrees
						.addAll(exteractUnrelevantSubtrees(matchedTree));
				negatedTrees.add(matchedTree.getTrees());
				// System.out.println("%%% Negated Tree After prunning: "
				// + matchedTree.toString());
			}
			deleteFormerMachedTrees(tree, tregexPattern);
		}
		// System.out.println(tree.toString());
		unnegatedTrees.add(tree);
		// System.out.println("Size of negated tree list: " +
		// negatedTrees.size());
		return negatedTrees;
	}

	private List<Tree> exteractUnrelevantSubtrees(TregexTree tree) {
		List<Tree> trees = new ArrayList<Tree>();
		trees.addAll(tree.getSBARs());
		tree.pruneSBAR();
		trees.addAll(tree.getParentheses());
		tree.pruneParentheses();
		trees.addAll(tree.getPPs());
		tree.prunePP();
		return trees;
	}

	private List<Tree> removePseudoNegation(List<Tree> trees) {
		for (int i = 0; i < trees.size(); i++) {
			if (negationAnalyzer.isPseudoNegation(trees.get(i))) {
				// System.out.println("--- pseudo negated tree : " +
				// tree.get(i));
				unnegatedTrees.add(trees.get(i));
				trees.remove(i);
			}
		}
		return trees;
	}

	private void addNegationSymbolToWords(List<Tree> trees) {
		for (Tree tree : trees) {
			List<Tree> leaves = tree.getLeaves();
			for (Tree leaf : leaves) {
				leaf.setValue("~" + leaf.value());
			}
			tree = exceptionWordsProcess(tree);
			// System.out.println("** Negated words : " + leaves.toString());
		}
	}

	// ***************
	private String[] exceptionWords = { "~but", "~," };

	private Tree exceptionWordsProcess(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		int leavesSize = leaves.size();
		for (String word : exceptionWords) {
			int index = getIndexOfExceptionWord(leaves, word);
			if (needToEdit(index, leavesSize, leaves)) {
				tree = editWrongNegatedWords(tree, index);
			}
		}
		return tree;
	}

	private int getIndexOfExceptionWord(List<Tree> trees, String word) {
		for (Tree tree : trees) {
			String leafValue = tree.value().toLowerCase();
			if (leafValue.equals(word)) {
				return trees.indexOf(tree);
			}
		}
		return 0;
	}

	private boolean needToEdit(int index, int size, List<Tree> trees) {
		if (isValidIndex(size, index)
				&& !isNegationCueAfterExceptionWords(index, trees)) {
			return true;
		}
		return false;
	}

	private boolean isValidIndex(int size, int index) {
		return index != 0 && index < size - 2;
	}

	private boolean isNegationCueAfterExceptionWords(int index,
			List<Tree> leaves) {
		for (int i = index; i < leaves.size(); i++) {
			String word = leaves.get(i).value().replaceFirst("~", "");
			if (NegationCues.isCues(word)) {
				return true;
			}
		}
		return false;
	}

	private Tree editWrongNegatedWords(Tree tree, int index) {// ???
		List<Tree> leaves = tree.getLeaves();
		if (leaves.get(index).value().equals("~,") && hasSameTag(index, tree)) {
			return tree;

		} else {
			leaves = removeNegationSymbol(index, leaves);
			return tree;
		}
	}

	private boolean hasSameTag(int index, Tree tree) {// check valid index ?
		List<Label> preterminalsNodes = tree.preTerminalYield();
		if (isValidIndex(preterminalsNodes.size(), index)) {
			return preterminalsNodes.get(index - 1).equals(
					preterminalsNodes.get(index + 1));
		}
		return false;
	}

	private List<Tree> removeNegationSymbol(int index, List<Tree> trees) {
		int i = index;
		while (i < trees.size()) {
			trees.get(i).setValue(trees.get(i).value().replace("~", ""));
			i++;
		}
		return trees;
	}

	private List<TaggedWord> getTaggedwords(List<Tree> trees) {
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		for (Tree tree : trees) {
			taggedWords.addAll(tree.taggedYield());
		}
		editingMisspellingWords(taggedWords);
		return taggedWords;
	}

	private List<TaggedWord> editingMisspellingWords(
			List<TaggedWord> taggedWords) { // dic
		for (TaggedWord taggedWord : taggedWords) {
			String word = taggedWord.word().toLowerCase();
			String tag = taggedWord.tag();
			if (word.equals("~ca") && tag.equals("MD")) {
				taggedWord.setWord("~can");
			} else if (word.equals("~wo") && tag.equals("MD")) {
				taggedWord.setWord("~will");
			}
		}
		return taggedWords;
	}

	private Tree deleteFormerMachedTrees(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head"); // prune
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	/* ************ */
	private WordList convertTagedWordsToWordList(List<TaggedWord> taggedSentence) {
		WordList wordsList = new WordList();
		for (TaggedWord taggedWord : taggedSentence) {
			wordsList.add(convertTaggedWordToWord(taggedWord));
		}
		return wordsList;
	}

	private Word convertTaggedWordToWord(TaggedWord taggedWord) {
		Word word = new Word();
		word.setWord(getStemmedToken(taggedWord));
		word.setTag(taggedWord.tag());
		return word;
	}

	private String getStemmedToken(TaggedWord taggedWord) {
		return (isVerbNeedsToStem(taggedWord.tag())) ? stem(taggedWord.word())
				: taggedWord.word();
	}

	private boolean isVerbNeedsToStem(String tagg) {
		return (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg
				.equals("VBZ"));
	}

	public WordList getListOfWordsWithCounts(List<WordList> sentences) {
		WordList globalWordList = new WordList();
		for (WordList sentence : sentences) {
			for (Word oneWord : sentence.getWords()) { // ??
				String word = oneWord.getWord().toLowerCase(); // Dec 9
				String tag = oneWord.getTag();
				if (!containWord(globalWordList, word, tag)) {
					globalWordList.add(new Word(word, tag, 1));
				}
			}
		}
		return globalWordList;
	}

	private boolean containWord(WordList wordList, String token, String tag) {// ?
		Word word = new Word(token, tag);
		for (Word word2 : wordList.getWords()) {
			if (word2.equal(word)) {
				word2.setCount(word2.getCount() + 1);
				return true;
			}
		}
		return false;
	}

}
