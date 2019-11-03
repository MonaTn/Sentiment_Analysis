package Negation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.Document;
import shared.PorterStemmer;
import shared.Word;
import shared.WordList;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
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

public class NegationPreprocessor_MinMinStrategy_081013 {

	private List<Tree> restOfTrees;
	private List<String> patterns;
	private List<Tree> treesToProcess;
	private List<Tree> temporaryListOfTrees;
	private PorterStemmer stemmer;
	private MaxentTagger taggerModel;
	private TregexPatternCompiler macros;
	private LexicalizedParser lexicalizedParser;
	private NegationPreprocessor negationProcessor;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public NegationPreprocessor_MinMinStrategy_081013() {
		initializeTagger();
		initializeLexicalizedParser();
		stemmer = new PorterStemmer();
		negationProcessor = new NegationPreprocessor();
		patterns = negationProcessor.getPatternsList();
		macros = negationProcessor.getMacros();
	}

	private void initializeLexicalizedParser() {
		String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
		lexicalizedParser = LexicalizedParser.loadModel(grammar, options);
	}

	private void initializeTagger() {
		try {
			taggerModel = new MaxentTagger(englishModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String stem(String word) {
		return stemmer.stem(word);
	}

	public void extractWordsListOfDocument(Document document)
			throws IOException {
		List<WordList> sentences = getTaggedWordListsOfDocument(document);
		document.setTotalSentencesCount(sentences.size());
		WordList wordsList = getListOfWordsWithCounts(sentences);
		document.setWordsList(wordsList);
	}

	private List<WordList> getTaggedWordListsOfDocument(Document document)
			throws IOException {
		List<WordList> sentences = new ArrayList<WordList>();
//		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(document.getAbsoluteFileName());
		DocumentPreprocessor documentPreprocessor = documentPreprocessor(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> taggedWordsOfSentence = getTaggedWords(sentence); // **
			WordList wordsList = convertTagedWordsToWordList(taggedWordsOfSentence);
			sentences.add(wordsList);
		}
		return sentences;
	}

	private DocumentPreprocessor documentPreprocessor(Document document) throws FileNotFoundException
			 {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String fileName = document.getAbsoluteFileName();
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				new FileReader(fileName));
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

	public void testProcessor(List<HasWord> sentence) {
		List<TaggedWord> taggedWords = getTaggedWords(sentence);
		System.out.println(" %%%%%% All Tagged words of Sentence : "
				+ Arrays.toString(taggedWords.toArray()));
	}

	// *******************************************************************************************
	private List<TaggedWord> getTaggedWords(List<HasWord> sentence) {
		if (negationProcessor.containNegationCues(sentence)) {
			System.out.println("___________________");
			System.out.println("Negation happend ! ==> " + sentence);
			return getTaggedWordsOfNegatedSentence(sentence);
		} else {
			return taggerModel.tagSentence(sentence);
		}
	}

	private List<TaggedWord> getTaggedWordsOfNegatedSentence(
			List<HasWord> sentence) {
		restOfTrees = new ArrayList<Tree>();
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		List<Tree> negatedTrees = new ArrayList<Tree>();
		Tree treeOfSentence = lexicalizedParser.parse(sentence);
		System.out
				.println("@@ Tree of sentence : " + treeOfSentence.toString());
		treesToProcess = new ArrayList<Tree>();
		treesToProcess.add(treeOfSentence);
		while (treesToProcess.size() != 0) {
			for (Tree tree : treesToProcess) {
				System.out.println("Tree to process : " + tree.toString());
				negatedTrees.addAll(exteractNegatedTrees(tree));
			}
			treesToProcess.clear();
			treesToProcess.addAll(temporaryListOfTrees);
			temporaryListOfTrees.clear();
		}
		if (negatedTrees.isEmpty()) {
			System.out.println("-- Negation tree is null!");
			return taggerModel.tagSentence(sentence);
		} else {
			List<Tree> realNegatedTrees = removePseudoNegation(negatedTrees);
			addNegationSymbolToWords(realNegatedTrees);
			taggedWords.addAll(getTaggedwords(realNegatedTrees));
			taggedWords.addAll(getTaggedwords(restOfTrees));
			// System.out.println(" %%%%%% All Tagged words of Sentence : "
			// + Arrays.toString(taggedWords.toArray()));
			return taggedWords;
		}
	}

	private List<Tree> exteractNegatedTrees(Tree tree) {
		List<Tree> negatedTrees = new ArrayList<Tree>();
		temporaryListOfTrees = new ArrayList<Tree>();
		for (String pattern : patterns) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				System.out.println("**Negated Pattern : " + pattern);
				TregexTree matchedTree = new TregexTree(matcher.getMatch());
				System.out.println("** Negated Tree : "
						+ matchedTree.toString());
				temporaryListOfTrees
						.addAll(exteractUnrelevantSubtrees(matchedTree));
				negatedTrees.add(matchedTree.getTree());
				System.out.println("%%% Negated Tree After prunning: "
						+ matchedTree.toString());
			}
			deleteSubTree(tree, tregexPattern);
		}
		// System.out.println(tree.toString());
		restOfTrees.add(tree);
		// System.out.println("Size of negated tree list: " +
		// negatedTrees.size());
		return negatedTrees;
	}

	private List<Tree> exteractUnrelevantSubtrees(TregexTree tree) {
		List<Tree> trees = new ArrayList<Tree>();
		trees.addAll(tree.getSBARs());
		tree.pruneSBAR();
		trees.addAll(tree.getParenthetical());
		tree.pruneParenthetical();
		trees.addAll(tree.getPPs());
		tree.prunePP();

		return trees;
	}

	private List<Tree> removePseudoNegation(List<Tree> tree) {
		for (int i = 0; i < tree.size(); i++) {
			if (negationProcessor.isPseudoNegation(tree.get(i))) {
				System.out.println("--- pseudo negated tree : " + tree.get(i));
				restOfTrees.add(tree.get(i));
				tree.remove(i);
				System.out
						.println("** Negated tree after remove pseudo part : "
								+ tree.toString());
			}
		}
		return tree;
	}

	private void addNegationSymbolToWords(List<Tree> trees) {
		for (Tree subTree : trees) {
			List<Tree> leaves = subTree.getLeaves();
			for (Tree leaf : leaves) {
				leaf.setValue("~" + leaf.value());
			}
			subTree = exceptionWordProcess(subTree, "~but");
			System.out.println("** Negated words : " + leaves.toString());
		}
	}

	// ***************
	private Tree exceptionWordProcess(Tree tree, String word) {
		List<Tree> leaves = tree.getLeaves();
		int index = getIndexOfExceptionWord(leaves, word);
		if (index <= 0 || isNegationCueAfterExceptionWords(index, leaves)) {
			return tree;
		} else {
			return editWrongNegatedWords(tree, index);
		}
	}

	private int getIndexOfExceptionWord(List<Tree> trees, String word) {
		for (Tree tree : trees) {
			String leafValue = tree.value().toLowerCase();
			if (leafValue.equals(word)) {
				return trees.indexOf(tree);
			}
		}
		return -1;
	}

	private boolean isNegationCueAfterExceptionWords(int index,
			List<Tree> leaves) {
		NegationPreprocessor negationProcessor = new NegationPreprocessor();
		for (int i = index; i < leaves.size(); i++) {
			String word = leaves.get(i).value().replaceFirst("~", "");
			if (negationProcessor.isNegationCues(word)) {
				return true;
			}
		}
		return false;
	}

	private Tree editWrongNegatedWords(Tree tree, int index) {
		int i = index;
		List<Tree> leaves = tree.getLeaves();
		while (i < leaves.size() && !leaves.get(i).value().equals(".")) {
			leaves.get(i).setValue(leaves.get(i).value().replace("~", ""));
			i++;
		}
		return tree;
	}

	private List<TaggedWord> editingMisspellingWords(
			List<TaggedWord> taggedWords) {
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

	// ***************

	private List<TaggedWord> getTaggedwords(List<Tree> trees) {
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		for (Tree subTree : trees) {
			taggedWords.addAll(subTree.taggedYield());
		}
		editingMisspellingWords(taggedWords);
		return taggedWords;
	}

	private Tree deleteSubTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private WordList convertTagedWordsToWordList(List<TaggedWord> tagSentence) {
		WordList wordsList = new WordList();
		for (TaggedWord taggedWord : tagSentence) {
			wordsList.add(convertTaggerWordToWord(taggedWord));
		}
		return wordsList;
	}

	private Word convertTaggerWordToWord(TaggedWord taggedWord) {
		Word word = new Word();
		word.setWord(getStemmedToken(taggedWord));
		word.setTag(taggedWord.tag());
		return word;
	}

	private String getStemmedToken(TaggedWord taggedWord) {
		return (isVerbThatNeedsToStem(taggedWord.tag())) ? stem(taggedWord
				.word()) : taggedWord.word();
	}

	private boolean isVerbThatNeedsToStem(String tagg) {
		return (tagg.equals("VBD") || tagg.equals("VBG") || tagg.equals("VBN") || tagg
				.equals("VBZ"));
	}

	public WordList getListOfWordsWithCounts(List<WordList> sentences) {
		WordList globalWordList = new WordList();
		for (WordList sentence : sentences) {
			for (Word newWord : sentence.getWords()) {
				String word = newWord.getWord();
				String tag = newWord.getTag();
				if (!containWords(globalWordList, word, tag)) {
					globalWordList.add(new Word(word, tag, 1));
				}
			}
		}
		return globalWordList;
	}

	private boolean containWords(WordList globalWordList, String token,
			String tag) {// ?
		Word word = new Word(token, tag);
		for (Word word2 : globalWordList.getWords()) {
			if (word2.equal(word)) {
				word2.setCount(word2.getCount() + 1);
				return true;
			}
		}
		return false;
	}
}
