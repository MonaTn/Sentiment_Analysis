package Negation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

// 2013.09.09
public class TextPreprocessorWithNegationFinder {

	private List<Tree> restOfTrees;
	private List<String> patterns;
	private List<Tree> mainTrees;
	private PorterStemmer stemmer;
	private MaxentTagger taggerModel;
	private TregexPatternCompiler macros;
	private LexicalizedParser lexicalizedParser;
	private NegationPreprocessor negationProcessor;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";

	public TextPreprocessorWithNegationFinder() {
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
		DocumentPreprocessor documentPreprocessor = documentPreprocessor(document);
		for (List<HasWord> sentence : documentPreprocessor) {
			List<TaggedWord> taggedWordsOfSentence = getTaggedWords(sentence); // **
			WordList wordsList = convertTagedWordsToWordList(taggedWordsOfSentence);
			sentences.add(wordsList);
		}
		return sentences;
	}

	private DocumentPreprocessor documentPreprocessor(Document document)
			throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String fileName = document.getAbsoluteFileName();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName), "utf-8"));
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				bufferReader);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
	}

	@SuppressWarnings("unused")
	private DocumentPreprocessor documentPreprocessor1(Document document) {
		return new DocumentPreprocessor(document.getAbsoluteFileName());
	}

	public void test(List<HasWord> sentence) {
		List<TaggedWord> taggedWordsOfSentence = getTaggedWords(sentence);
		System.out.println(" %%%%%% All Tagged words of Sentence : "
				+ Arrays.toString(taggedWordsOfSentence.toArray()));
	}

	// *******************************************************************************************
	public List<TaggedWord> getTaggedWords(List<HasWord> sentence) {
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
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		List<Tree> negatedTrees = new ArrayList<Tree>();
		Tree treeOfSentence = lexicalizedParser.parse(sentence);
		System.out
				.println("@@ Tree of sentence : " + treeOfSentence.toString());
		mainTrees = Collections.synchronizedList(new ArrayList<Tree>());
		restOfTrees = new ArrayList<Tree>();
//		mainTrees = new CopyOnWriteArrayList<Tree>();
		
		mainTrees.add(treeOfSentence);
		Iterator iterator = mainTrees.iterator();
		while (iterator.hasNext()) {
			Tree testTree = (Tree) iterator.next();
			negatedTrees.addAll(exteractNegatedSubTrees(testTree));
		}
		if (negatedTrees.isEmpty()) {
			System.out.println("-- Negation tree is null!");
			return taggerModel.tagSentence(sentence);
		} else {
			List<Tree> realNegatedTree = getRealNegatedTrees(negatedTrees);
			addNegationSymbolToWords(realNegatedTree);
			taggedWords.addAll(getTaggedwords(realNegatedTree));
			taggedWords.addAll(getTaggedwords(restOfTrees));
			return taggedWords;
		}
	}

	public List<Tree> exteractNegatedSubTrees(Tree tree) {
		System.out.println("****************Tree for neg Proccessing : "
				+ tree.toString());
		List<Tree> negatedTrees = new ArrayList<Tree>();
		for (String pattern : patterns) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				System.out.println("**Negated Pattern : " + pattern);
				TregexTree subTree = new TregexTree(matcher.getMatch());
				System.out.println("** Negated Tree : ");
				subTree.toPrint();
				mainTrees.addAll(subTree.getSBARs());
				System.out.println("After adding SBAR : " + mainTrees.size());
				mainTrees.addAll(subTree.getParenthetical());
				System.out.println("After adding () : " + mainTrees.size());
				mainTrees.addAll(subTree.getPPs());
				System.out.println("After adding PP : " + mainTrees.size());
				negatedTrees.add(subTree.prune());
				System.out.println("%%% Negated Tree After prunning: ");
				subTree.toPrint();
			}
			deleteSubTree(tree, tregexPattern);
			synchronized (mainTrees) {
				Iterator i = mainTrees.iterator(); // Must be in synchronized block
				while (i.hasNext())
				foo(i.next());
		
			}
		}
		restOfTrees.add(tree);
		return negatedTrees;
	}

	
	
	private void foo(Object next) {
		// TODO Auto-generated method stub
		
	}

	private List<Tree> getRealNegatedTrees(List<Tree> trees) {
		for (int i = 0; i < trees.size(); i++) {
			if (negationProcessor.isPseudoNegation(trees.get(i))) {
				System.out.println("--- pseudo negated tree : " + trees.get(i));
				restOfTrees.add(trees.get(i));
				trees.remove(i);
				System.out
						.println("** Negated tree after remove pseudo part : "
								+ trees.toString());
			}
		}
		return trees;
	}

	private void addNegationSymbolToWords(List<Tree> trees) {
		for (Tree subTree : trees) {
			List<Tree> leaves = subTree.getLeaves();
			for (Tree leave : leaves) {
				leave.setValue("~" + leave.value());
			}
			System.out.println("** Negated words : " + leaves.toString());
		}
	}

	private List<TaggedWord> getTaggedwords(List<Tree> trees) {
		List<TaggedWord> taggedWords = new ArrayList<TaggedWord>();
		for (Tree subTree : trees) {
			taggedWords.addAll(subTree.taggedYield());
		}
		return taggedWords;
	}

	public Tree deleteSubTree(Tree tree, TregexPattern pattern) { // ***************
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private void printOutTree(List<Tree> trees, String treesName) {
		System.out
				.println(treesName
						+ "_____________________________________________________________");
		for (Tree tree : trees) {
			System.out.println(tree.toString());
		}
		System.out
				.println(treesName
						+ "_____________________________________________________________");
	}

	public List<Tree> xxx(Tree tree) {
		List<Tree> trees = new ArrayList<Tree>();
		trees.addAll(extractSBARsSubtrees(tree));
		trees.addAll(extractParentheticalSubtrees(tree));
		return trees;
	}

	public List<Tree> extractSBARsSubtrees(Tree tree) {
		String pattern = "SBAR=sbar > __";
		return findSubTreeBasedOnAPattern(tree, pattern);
	}

	public List<Tree> extractParentheticalSubtrees(Tree tree) {
		String pattern = "PRN=prn > __";
		return findSubTreeBasedOnAPattern(tree, pattern);
	}

	public List<Tree> findSubTreeBasedOnAPattern(Tree tree, String pattern) {
		List<Tree> subTrees = new ArrayList<Tree>();
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TregexMatcher matcher = tregexPattern.matcher(tree);
		while (matcher.findNextMatchingNode()) {
			subTrees.add(matcher.getMatch());
		}
		printOutTree(subTrees, "* SUB Trees ");
		return subTrees;
	}

	private Tree applyPatternsOnTree(Tree tree, String pattern,
			String tsurgeonPattern) {
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TsurgeonPattern surgeon = Tsurgeon.parseOperation(tsurgeonPattern);
		Tsurgeon.processPattern(tregexPattern, surgeon, tree);
		return tree;
	}

	public Tree pruneSBAR(Tree tree) {
		String pattern = "SBAR=sbar > __";
		String tsurgeonPattern = "prune sbar";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public Tree prunePP(Tree tree) {
		String pattern = "PP=pp >__";
		String tsurgeonPattern = "prune pp";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public Tree pruneParenthetical(Tree tree) {
		String pattern = "PRN=prn >__";
		String tsurgeonPattern = "prune prn";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public Tree prune(Tree tree) {
		pruneSBAR(tree);
		pruneParenthetical(tree);
		prunePP(tree);
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

// public void pruneTree(Tree tree) {
// System.out.println("@@ Main Tree : " + tree.toString());
// System.out.println("Size of Tree : " + tree.numChildren());
// List<Tree> extra = new ArrayList<Tree>();
// for (int i = 0; i < tree.numChildren(); i++) {
// System.out.println("get " + i + " : " + tree.getChild(i).value());
// if (tree.getChild(i).value().equals("SBAR")
// || tree.getChild(i).value().equals("S")) {
// extra.add(tree.getChild(i));
// System.out.println("Extra at " + i + " is "
// + Arrays.toString(extra.toArray()));
// tree.removeChild(i);
// }
// }
// System.out.println("Tree: " + tree.toString());
// System.out.println("Extra: " + Arrays.toString(extra.toArray()));
// }
// private List<Tree> extractNegatedSubTree(Tree tree) {
// // System.out.println ("Tree to process : " + tree.toString());
// List<Tree> negatedTrees = new ArrayList<Tree>();
// for (String pattern : patterns) {
// TregexPattern tregexPattern = macros.compile(pattern);
// TregexMatcher matcher = tregexPattern.matcher(tree);
// while (matcher.findNextMatchingNode()) {
// Tree subTree = matcher.getMatch();
// negatedTrees.add(subTree);
// System.out.println("**Negated Pattern : " + pattern);
// System.out.println("** Negated Tree : " + subTree.toString());
// }
// deleteSubTree(tree, tregexPattern);
// }
// // listOfRestOfTrees.add(tree);
// // System.out.println("-- Rest of the tree : " + tree.toString());
// return negatedTrees;
// }
// private boolean isContainSBAR(Tree tree) {
// String pattern = "SBAR=sbar > __";
// TregexPattern tregexPattern = TregexPattern.compile(pattern);
// TregexMatcher matcher = tregexPattern.matcher(tree);
// return matcher.find();
// }
//
// private List<Tree> extractNegatedSubTree1(Tree mainTree) {
// System.out.println("@@ Main Tree : " + mainTree.toString());
// List<Tree> negatedTrees = new ArrayList<Tree>();
// treesToFindNegation = new ArrayList<Tree>();
// treesToFindNegation.add(mainTree);
// System.out.println (treesToFindNegation.size());
// while(treesToFindNegation.iterator().hasNext()) {
// System.out.println("===== Size :"+treesToFindNegation.size());
// Tree tree =
// treesToFindNegation.iterator().next();//.get(treesToFindNegation.size()-1);
// for (String pattern : patternsList) {
// TregexPattern tregexPattern = macros.compile(pattern);
// TregexMatcher matcher = tregexPattern.matcher(tree);
// while (matcher.findNextMatchingNode()) {
// Tree subTree = matcher.getMatch();
// subTree = verifyContainSBAR1(subTree);
// negatedTrees.add(subTree);
// System.out.println("**Negated Pattern : " + pattern);
// System.out.println("** Negated Tree : " + subTree.toString());
// }
// deleteSubTree(tree, tregexPattern);
//
// }
// }
// // listOfRestOfTrees.add(mainTree);
// // System.out.println("-- Rest of the tree : " + tree.toString());
// return negatedTrees;
// }

// private Tree verifyContainSBAR1(Tree tree) {
// for (int i = 0; i < tree.numChildren(); i++) {
// if (tree.getChild(i).value().equals("SBAR")) {
// treesToFindNegation.add(tree.getChild(i));
// tree.removeChild(i);
// }
// }
// return tree;
// }