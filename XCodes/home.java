package Negation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.Word;
import shared.WordList;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class home {

	static List<String> patternsList;
	static TregexPatternCompiler macros;
	static LexicalizedParser parser;

	public static void main(String[] args) {
		patternsList = NegationElements.getPatternsList(); 
		macros = NegationElements.getMacros();

		String fileName = "../../TrainingSet/Test/test3.txt";

		parser = LexicalizedParser
				.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");


		if (fileName != "") {
			List<Tree> negationTree = buildNegationTree(fileName);
		} else {
			System.out.println("Please select a file!");
		}
		System.out
		.println("___________________________________________________________________________");
	}




	private static List<Tree> buildNegationTree(String filename) {
		List<Tree> negationTree = new ArrayList<Tree>();
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) { // **
			System.out.println(sentence.toString());
			Tree pennTree = parser.apply(sentence);
			Tree negatedSubTree = extractNegatedSubTree(pennTree);
			if (negatedSubTree != null) {
				negationTree.add(negatedSubTree);
				WordList wordList = pruneTree (negatedSubTree);
				wordList.toPrint();
			}
			System.out
			.println("___________________________________________________________________________");
		}
		return negationTree;
	}
	private static Tree extractNegatedSubTree(Tree parse) {
		
//		System.out.println("Main Tree ***** " + parse.toString());
//		parse.pennPrint();
		for (String pattern : patternsList) {
			TregexPattern negationRegEX = macros.compile(pattern);
			TregexMatcher matcher = negationRegEX.matcher(parse);
			if (matcher.find()) {
				Tree subTree = matcher.getMatch();
				// System.out.println("Pattern:" + pattern);
				System.out.println("Negated sub tree: " + subTree.toString());
				return subTree;
			}
		}
		return null;
	}
//	Tree[] children = tree.children();
	// Tree test = tree.getNodeNumber(7);

	public static void prun(List<Tree> treeList) {
		for (Tree subTree : treeList) {
			List<Tree> listOfNodeAfterNegatedTerm = new ArrayList<Tree>();
			listOfNodeAfterNegatedTerm = subTree.getChild(2).getChildrenAsList();
			System.out.println ("Children as List : "+Arrays.toString(listOfNodeAfterNegatedTerm.toArray()));
			int sizeOfNegatedNodes = listOfNodeAfterNegatedTerm.size();
			for (Tree negatedNode : listOfNodeAfterNegatedTerm) {
				String valueLable = negatedNode.label().value();
				System.out.println("Negated new: " + valueLable);
				if (valueLable.equals("S") | valueLable.equals("PP")) {
				negatedNode.remove(valueLable);
				System.out.println(valueLable +" is deleted!!");
				}
				System.out.println(negatedNode.toString());
			}
//			for (int i = 0; i < sizeOfNegatedNodes ; i++) {
//				String valueLable = listOfNodeAfterNegatedTerm.get(i).label().value();
//				System.out.println("***************" + valueLable);
//				if (valueLable.equals("S") | valueLable.equals("PP")) {
//					System.out.println("Before remove "+ valueLable+" : " + listOfNodeAfterNegatedTerm.toString());
//					listOfNodeAfterNegatedTerm.remove(i);
//				} else {
//					List<TaggedWord> tag = listOfNodeAfterNegatedTerm.get(i).taggedYield();
//					for (TaggedWord ss : tag) {
//						System.out.println(ss);
//					}
//				}
//			}
			System.out.println("Final  " + listOfNodeAfterNegatedTerm.toString());
			System.out
			.println("___________________________________________________________________________");
		}
	}
	
	
	public static WordList pruneTree(Tree tree) {
		WordList wordslist = new WordList();
		int i ;
			if (containAuxVerb(tree)) {
				System.out.println ("************************** Aux find !");
				 i =2;
			} else {
				 i = 0;
			}
			List<Tree> trees = tree.getChildrenAsList();
			for (int j = i ; j < trees.size() ; j++) {
				List<TaggedWord> words = trees.get(j).taggedYield();
				System.out.println(Arrays.toString(words.toArray()));
				for (TaggedWord taggedWord : words) {
					if (taggedWord.tag().contains("VB") || taggedWord.tag().contains("JJ") || taggedWord.tag().contains("MD")) {
						Word word = new Word();
						word.setWord("~"+taggedWord.word());
						word.setTag(taggedWord.tag());
						wordslist.add(word);
						System.out.println("------"+ taggedWord.word()+" , "+taggedWord.tag());
					}
				}
			}
				return wordslist;
			
//			List<Tree> children = subTree.getChildrenAsList();
//			if (children.get(0).contains("VB")) {
//				
//			}
//			List<Tree> listOfNodeAfterNegatedTerm = new ArrayList<Tree>();
//			listOfNodeAfterNegatedTerm = subTree.getChild(2).getChildrenAsList();
//			System.out.println ("Children as List : "+Arrays.toString(listOfNodeAfterNegatedTerm.toArray()));
//			int sizeOfNegatedNodes = listOfNodeAfterNegatedTerm.size();
//			for (Tree negatedNode : listOfNodeAfterNegatedTerm) {
//				String valueLable = negatedNode.label().value();
//				System.out.println("Negated new: " + valueLable);
//				if (valueLable.equals("S") | valueLable.equals("PP")) {
//				negatedNode.remove(valueLable);
//				System.out.println(valueLable +" is deleted!!");
//				}
//				System.out.println(negatedNode.toString());
//			}
////			for (int i = 0; i < sizeOfNegatedNodes ; i++) {
////				String valueLable = listOfNodeAfterNegatedTerm.get(i).label().value();
////				System.out.println("***************" + valueLable);
////				if (valueLable.equals("S") | valueLable.equals("PP")) {
////					System.out.println("Before remove "+ valueLable+" : " + listOfNodeAfterNegatedTerm.toString());
////					listOfNodeAfterNegatedTerm.remove(i);
////				} else {
////					List<TaggedWord> tag = listOfNodeAfterNegatedTerm.get(i).taggedYield();
////					for (TaggedWord ss : tag) {
////						System.out.println(ss);
////					}
////				}
////			}
//			System.out.println("Final  " + listOfNodeAfterNegatedTerm.toString());
//			System.out
//			.println("___________________________________________________________________________");
		}

	private static boolean containAuxVerb(Tree tree) {
		TregexPattern auxVerbPattern = macros.compile("@VB $. (@NOT $. VP)");
		TregexMatcher matcher = auxVerbPattern.matcher(tree);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
		
		
	}
		
	}


