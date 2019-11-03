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

public class TextPreprocessorWithNegationFinder_0609013 {

	private List<Tree> restOfTrees;
	private List<String> patterns;
	private List<Tree> mainTrees;
	private PorterStemmer stemmer;
	private MaxentTagger taggerModel;
	private TregexPatternCompiler macros;
	private LexicalizedParser lexicalizedParser;
	private NegationPreprocessor negationProcessor;
	private final String englishModel = "TaggerModels/english-bidirectional-distsim.tagger";
	
	public TextPreprocessorWithNegationFinder_0609013() {
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

	public void getWordsListsOfSentences(Document document) throws IOException {
		List<WordList> sentences = getSentencesFromDocument(document);
		document.setTotalSentencesCount(sentences.size());
		WordList wordsList = getListOfWordsWithCounts(sentences);
		document.setWordsList(wordsList);
	}

	public List<WordList> getSentencesFromDocument(Document document)
			throws IOException {
		List<WordList> sentences = new ArrayList<WordList>();
		DocumentPreprocessor documentPreprocessor = documentPreprocessing(document);
		for (List<HasWord> sentence : documentPreprocessor) {
//			System.out.println(sentence.toArray().toString());
//			Tree treeOfSentence = lexicalizedParser.parse(sentence);
//			System.out.println("@@ Tree of sentence : " + treeOfSentence.toString());
			List<TaggedWord> tagedSentence = getTaggedWords(sentence);
			WordList wordList = convertTagedsentenceToWordList(tagedSentence);
			sentences.add(wordList);
		}
		return sentences;
	}

	private DocumentPreprocessor documentPreprocessing(Document document)
			throws IOException {
		TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "untokenizable=noneKeep");
		String absoluteDocumentName = document.getPath() + "/"
				+ document.getName();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(absoluteDocumentName), "utf-8"));
		DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(
				bufferReader);
		documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
		return documentPreprocessor;
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
		List<TaggedWord> tagedWords = new ArrayList<TaggedWord>();
		List<Tree> negatedTrees = new ArrayList<Tree>();
		Tree treeOfSentence = lexicalizedParser.parse(sentence);
		System.out.println("@@ Tree of sentence : " + treeOfSentence.toString());
		mainTrees = Collections.synchronizedList(new ArrayList<Tree>());
		mainTrees.add(treeOfSentence);
		Iterator<Tree> iterator = mainTrees.iterator();
		while (iterator.hasNext()) {
			negatedTrees.addAll(exteractNegatedSubTrees(iterator.next()));
		}
		if (negatedTrees.isEmpty()) {
			System.out.println("-- Negation tree is null!");
			return taggerModel.tagSentence(sentence);
		} else {
			List<Tree> realNegatedTree = getRealNegatedTrees(negatedTrees);
			addNegationSymbolToWords(realNegatedTree);
			tagedWords.addAll(getTaggedwords(realNegatedTree));
//			tagedWords.addAll(getTaggedwords(restOfTrees));
//			System.out.println(" %%%%%% All Tagged words of Sentence : "
//					+ Arrays.toString(tagedWords.toArray()));
			return tagedWords;
		}
	}
	
	private List<Tree> extractNegatedSubTree(Tree tree) {
//		System.out.println ("Tree to process : " + tree.toString());
		List<Tree> negatedTrees = new ArrayList<Tree>();
			for (String pattern : patterns) {
				TregexPattern tregexPattern = macros.compile(pattern);
				TregexMatcher matcher = tregexPattern.matcher(tree);
				while (matcher.findNextMatchingNode()) {
					Tree subTree = matcher.getMatch();
					negatedTrees.add(subTree);
					System.out.println("**Negated Pattern : " + pattern);
					System.out.println("** Negated Tree : " + subTree.toString());
				}
				deleteSubTree(tree, tregexPattern);
			}
//		listOfRestOfTrees.add(tree);
//		System.out.println("-- Rest of the tree : " + tree.toString());
		return negatedTrees;
	}
//	private boolean isContainSBAR(Tree tree) {
//		String pattern = "SBAR=sbar > __";
//		TregexPattern tregexPattern = TregexPattern.compile(pattern);
//		TregexMatcher matcher = tregexPattern.matcher(tree);
//		return matcher.find();
//	}
//
//	private List<Tree> extractNegatedSubTree1(Tree mainTree) {
//		System.out.println("@@ Main Tree : " + mainTree.toString());
//		List<Tree> negatedTrees = new ArrayList<Tree>();
//		treesToFindNegation = new ArrayList<Tree>();
//		treesToFindNegation.add(mainTree);
//		System.out.println (treesToFindNegation.size());
//		while(treesToFindNegation.iterator().hasNext()) {
//			System.out.println("===== Size :"+treesToFindNegation.size());
//			Tree tree = treesToFindNegation.iterator().next();//.get(treesToFindNegation.size()-1);
//			for (String pattern : patternsList) {
//				TregexPattern tregexPattern = macros.compile(pattern);
//				TregexMatcher matcher = tregexPattern.matcher(tree);
//				while (matcher.findNextMatchingNode()) {
//					Tree subTree = matcher.getMatch();
//					subTree = verifyContainSBAR1(subTree);
//					negatedTrees.add(subTree);
//					System.out.println("**Negated Pattern : " + pattern);
//					System.out.println("** Negated Tree : " + subTree.toString());
//				}
//				deleteSubTree(tree, tregexPattern);
//				
//			}
//		}
////		listOfRestOfTrees.add(mainTree);
////		System.out.println("-- Rest of the tree : " + tree.toString());
//		return negatedTrees;
//	}

//	private Tree verifyContainSBAR1(Tree tree) {
//		for (int i = 0; i < tree.numChildren(); i++) {
//			if (tree.getChild(i).value().equals("SBAR")) {
//				treesToFindNegation.add(tree.getChild(i));
//				tree.removeChild(i);
//			}
//		}
//		return tree;
//	}

	private List<Tree> getRealNegatedTrees(List<Tree> tree) {
		for (int i = 0; i < tree.size(); i++) {
			if (negationProcessor.isPseudoNegation(tree.get(i))) {
				System.out.println("--- pseudo negated tree : " + tree.get(i));
				restOfTrees.add(tree.get(i));
				tree.remove(i);
				System.out
						.println("** Negated tree after remove pseudo part : "
								+ tree.toString());
			}
//				else {
//				pruneTree(tree.get(i));
//			}
		}
		return tree;
	}

	public void pruneTree(Tree tree) {
		System.out.println("@@ Main Tree : " + tree.toString());
		System.out.println("Size of Tree : " + tree.numChildren());
		List<Tree> extra = new ArrayList<Tree>();
		for (int i = 0; i < tree.numChildren(); i++) {
			System.out.println ("get " + i + " : " + tree.getChild(i).value());
			if (tree.getChild(i).value().equals("SBAR")
					|| tree.getChild(i).value().equals("S")) {
				extra.add(tree.getChild(i));
				System.out.println("Extra at " + i + " is " + Arrays.toString(extra.toArray()));
				tree.removeChild(i);
			}
		}
		System.out.println("Tree: " + tree.toString());
		System.out.println("Extra: " + Arrays.toString(extra.toArray()));
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

	public   Tree deleteSubTree(Tree tree, TregexPattern pattern) {
		TsurgeonPattern surgeon = Tsurgeon.parseOperation("delete head");
		Tsurgeon.processPattern(pattern, surgeon, tree);
		return tree;
	}

	// *******************************************************************************************
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public   List<Tree> exteractNegatedSubTrees(Tree tree ) {
		List<Tree> negatedTrees = new ArrayList<Tree>();
		for (String pattern : patterns) {
			TregexPattern tregexPattern = macros.compile(pattern);
			TregexMatcher matcher = tregexPattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				TregexTree subTree = new TregexTree();
				subTree.setTree(matcher.getMatch());
				mainTrees.addAll(subTree.getSBARs());
				mainTrees.addAll(subTree.getParenthetical());
				negatedTrees.add(subTree.prune());
				subTree.toPrint();
			}
		}
		return negatedTrees;
	}

	private   void printOutTree(List<Tree> trees, String treesName) {
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

	public   List<Tree> xxx(Tree tree) {
		List<Tree> trees = new ArrayList<Tree>();
		trees.addAll(extractSBARsSubtrees(tree));
		trees.addAll(extractParentheticalSubtrees(tree));
		return trees;
	}

	public   List<Tree> extractSBARsSubtrees(Tree tree) {
		String pattern = "SBAR=sbar > __";
		return findSubTreeBasedOnAPattern(tree, pattern);
	}

	public   List<Tree> extractParentheticalSubtrees(Tree tree) {
		String pattern = "PRN=prn > __";
		return findSubTreeBasedOnAPattern(tree, pattern);
	}

	public   List<Tree> findSubTreeBasedOnAPattern(Tree tree,
			String pattern) {
		List<Tree> subTrees = new ArrayList<Tree>();
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TregexMatcher matcher = tregexPattern.matcher(tree);
		while (matcher.findNextMatchingNode()) {
			subTrees.add(matcher.getMatch());
		}
		printOutTree(subTrees, "* SUB Trees ");
		return subTrees;
	}

	private   Tree applyPatternsOnTree(Tree tree, String pattern,
			String tsurgeonPattern) {
		TregexPattern tregexPattern = TregexPattern.compile(pattern);
		TsurgeonPattern surgeon = Tsurgeon.parseOperation(tsurgeonPattern);
		Tsurgeon.processPattern(tregexPattern, surgeon, tree);
		return tree;
	}

	public   Tree pruneSBAR(Tree tree) {
		String pattern = "SBAR=sbar > __";
		String tsurgeonPattern = "prune sbar";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public   Tree prunePP(Tree tree) {
		String pattern = "PP=pp >__";
		String tsurgeonPattern = "prune pp";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public   Tree pruneParenthetical(Tree tree) {
		String pattern = "PRN=prn >__";
		String tsurgeonPattern = "prune prn";
		applyPatternsOnTree(tree, pattern, tsurgeonPattern);
		return tree;
	}

	public   Tree prune(Tree tree) {
		pruneSBAR(tree);
		pruneParenthetical(tree);
		prunePP(tree);
		return tree;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private WordList convertTagedsentenceToWordList(List<TaggedWord> tagSentence) {
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
