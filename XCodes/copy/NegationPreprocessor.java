package X.Negation.copy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class NegationPreprocessor {
	
	public   TregexPatternCompiler getMacros() {
		TregexPatternCompiler macros = new TregexPatternCompiler();
		macros.addMacro("@VB", "/^VB/");
		macros.addMacro("@AD", "ADVP|ADJP");
		macros.addMacro("@PRN", "NP|ADJP|ADVP|S|PP");
		macros.addMacro("@SPRN", "NP|ADJP|ADVP|S|PP");
		macros.addMacro("@RB-NOT", "(RB=rb < @NOT)");
		macros.addMacro("@RB-T", "(RB < @T)");
		macros.addMacro("@The", "(DT < The|THE|the)");
		macros.addMacro("@Neither", "(__ < NEITHER|Neither|neither)");
		macros.addMacro("@Nor", "(__ < NOR|Nor|nor)");
		macros.addMacro("@Aux", "have|has|had|'ve|’ve|'s|’s|s|do|Do|DO|does|did");
		macros.addMacro("@Be", "am|'m|’m|is|are|was|were|'s|’s|s|'re|’re");
		macros.addMacro("@WILL", "will|wo|would");
		macros.addMacro("@CAN", "can|Can|CAN");
		macros.addMacro("@NADV", "never|Never|NEVER|barely|scarcely|hardly");
		macros.addMacro("@Few", "FEW|Few|few");
		macros.addMacro("@Little", "little|Little|LITTLE");
		macros.addMacro("@Only", "Only|ONLY|only|SOLELY|Solely|solely");
		macros.addMacro("@Nothing", "Nothing|NOTHING|nothing");
		macros.addMacro("@Nowhere", "NOWHERE|Nowhere|nowhere");
		macros.addMacro("@NoOnes", "One|one|One|Person|person|PERSON");
		macros.addMacro("@Nobody", "NOBODY|Nobody|nobody");
		macros.addMacro("@Anybody", "ANYBODY|Anybody|anybody");
		macros.addMacro("@Anyone", "Anyone|anyone|ANYONE");
		macros.addMacro("@None", "None|NONE|none");
		macros.addMacro("@NOT", "NOT|Not|not|n’t|n't|N'T|N’T|n't");
		macros.addMacro("@NOOT", "NOT|Not|not");
		macros.addMacro("@NO", "NO|No|no");
		macros.addMacro("@T", "’t|'t|t|’T|'T|T");
		macros.addMacro("@A", "(DT < /[Aa]/)");
		return macros;
	}

	public   List<String> getPatternsList() {
		List<String> patternsList =  new ArrayList<String>();
		/** Without **/
		patternsList.add("PP=head < IN=in : (=in < /[Ww]ithout/)");
		/** Few **/ 
		patternsList.add("NP=head < __=label : (=label < @Few !$, @A)");
		patternsList.add("VP=head << NP=np : (=np << JJ=adj) : (=adj < @Few !$, @A)"); // in object
		patternsList.add("VP=head $, NP=np : (=np << JJ=adj) : (=adj < @Few !$, @A) "); // in subject
		/** Little **/
		patternsList.add("VP=head < @VB=vb < NP=np : (=vb $. =np) : (=np <+(NP) JJ=adj) : (=adj < @Little !$, @A !$, CD !$, JJ !$ @The !$. NNP)"); // in subject
//		patternsList.add("VP=head < @AD=ad : (=ad << RB=rb) : (=rb < @Little=little) : (@A !. =little)"); // in object
//		patternsList.add("VP=head $. @AD=ad : (=ad << RB=rb) : (=rb < @Little=little) : (@A !. =little)"); // in subject
//		patternsList.add("VP=head << NP=np : (=np << JJ=adj) : (=adj < @Little=little) : (@A !. =little)"); // in object
//		patternsList.add("VP=head $, NP=np : (=np << JJ=adj) : (=adj < @Little=little) : (@A !. =little)"); // in subject
//		patternsList.add("VP=head << @AD=ad : (=ad << RB=rb) : (=rb < @Little !$, @A)"); // in object
//		patternsList.add("VP=head $, @AD=ad : (=ad << RB=rb) : (=rb < @Little !$, @A)"); // in subject
//		patternsList.add("VP=head < NP=np : (=np << JJ=adj) : (=adj < @Little !$, @A !$, CD)"); // in object
//		patternsList.add("VP=head < @VB=vb < NP=np : (=vb $. =np) : (=np < (__=label < @Little)) : (=label !$, @A !$, CD !$. JJ)");
		/** Nothing **/
		patternsList.add("VP=head , NP=np : (=np << NN=nn) : (=nn < @Nothing)"); // in subject
		patternsList.add("VP=head < @VB=vb < NP=np : (=np <+(NP) (NN < @Nothing))");
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=ad ,, =vb < (NN < @Nothing))");
		patternsList.add("VP=head < @VB=vb < @PRN=prn : (=prn << NN=nn) : (=nn < @Nothing)"); // in object
		patternsList.add("NP=head < __=label : (=label < @Nothing)"); //?
		patternsList.add("VP=head , @Nothing");
		patternsList.add("VP=head ,+(NP) @AD=ad : (=ad < __=label) : (=label < @Nothing)");
		/** Nobody **/
		patternsList.add("VP=head , NP=np : (=np << @Nobody)"); // in subject
		patternsList.add("VP=head < @PRN-SBAR=prn : (=prn << @Nobody)"); // in object
		/** None of **/
		patternsList.add("VP=head $,, NP=np : (=np <+(NP) __=label) : ( =label < @None=none) : (=none ?. of|Of|OF)"); // in subject 
		patternsList.add("VP=head < NP=np : (=np <+(NP) __=label) : (=label < (@None ?. of|Of|OF))"); // in object
		patternsList.add("NP=head << NP=np << PP=pp : (=np <+(NP) __=label) : (=label < @None) : (=pp << of|Of|OF) : (=np $. =pp)");
		/** Nowhere **/
		patternsList.add("VP=head < @VB < @AD=ad : (=ad << RB=rb) : (=rb < @Nowhere)"); // as adverb
		patternsList.add("VP=head < @VB < NP=np : (=np << JJ=adj) : (=adj < @Nowhere)"); // as adjective
		/** Neither Nor structure **/ 
		patternsList.add("VP=head $, NP=np : (=np << @Neither << @Nor)");// Neither & nor structure by NP
		patternsList.add("VP=head $, @AD=ad : (=ad << @Neither !<< @Nor)");// Neither & nor structure by ADVP
		patternsList.add("VP=head < @AD=ad : (=ad << @Neither << @Nor)"); //new , VP < ADJP #1
		patternsList.add("VP=head < @VB <+(S) NP=np : (=np << @Neither << @Nor)"); //NEW, NEITHER & NOR INSIDE OF VP BY NP #2
		patternsList.add("VP=head < @VB < @SPRN=prn : (=prn << @Neither ?<< @Nor)");// Neither & nor structure INSTEAD OF #1 & #2 
		patternsList.add("VP=head $, NP=np !< @Nor: (=np << @Neither !<<@Nor)");// Neither structure
		patternsList.add("VP=head , @Nor"); // Nor structure ???? example
		patternsList.add("NP=head < @Neither ?<@Nor");
		patternsList.add("NP=head < @Nor !< @Neither"); //MODIFY
		/** Short answer **/
		patternsList.add("VP=head < @VB=vb [<- @RB-NOT | <- @Neither] : (=vb < @Be|@Aux)"); 
		patternsList.add("VP=head < MD [<- @RB-NOT | <- @Neither]"); 
		/** Aux (have/do) not VP **/
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Aux $. @RB-NOT)"); 
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Aux .. RB=rb) : (=rb < @NOT=not) : (=not >> =head)"); // !>> S)"); // VP-aux (have/do) not VP
		/** TO Be not PRD/VP **/
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Be $. @RB-NOT)"); // VP-be not VP => Be is aux verb
		patternsList.add("VP=head < @VB=vb < @PRN : (=vb < @Be $. @RB-NOT)"); // VP-be not PRD => Be is main verb 
		patternsList.add("VP=head < @VB=vb < @PRN : (=vb < @Be=be .. RB=rb) : (=rb < @NOT ,, =be ) : (=rb > =head)");
		patternsList.add("VP=head < @VB=vb < RB=rb < VP : (=vb < @Be .. (=rb < @NOT)) "); // VP-be not VP => Be is aux verb
		patternsList.add("VP=head < @VB=vb < RB=rb : (=vb $.. =rb) : (=rb < @NOT)");
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=vb < @Be $. =ad) : (=ad < @RB-NOT)");
		/** Modal **/ 
		patternsList.add("VP=head < MD=md < VP : (=md $. @RB-NOT)"); 
		patternsList.add("VP=head < MD=md < VP : (=md $.. @AD=ad) : (=ad < @RB-NOT)");
		patternsList.add("VP=head < MD=md < VP : (=md $. @AD=ad) : (=ad $. @RB-NOT)");
		patternsList.add("VP=head < MD=md < VP : (=md < @CAN $. @AD=ad) : (=ad < @RB-T)");
		/** Negative adverb of frequency **/ 
		patternsList.add("VP=head < MD < @AD=ad : (=ad < RB=rb) : (=rb < @NADV)");
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=vb < @Be) : (=ad < RB=rb ,, =vb) : (=rb < @NADV)"); // with to be
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=vb $, =ad) : (= ad << @NADV)");
		patternsList.add("VP=head $,, @AD=ad : (=ad < RB=rb) : (=rb < @NADV)"); 
		patternsList.add("VP=head > S=s : (=s $, @AD=ad) : (=ad < RB=rb) : (=rb < @NADV)");
		/** No **/
		patternsList.add("VP=head < NP=np : (=np <+(NP) (__ < @NO))"); // using "no" after verb as negation *
		patternsList.add("VP=head $,, NP=np : (=np <1 (DT < @NO)) : (=np <2 (NN < @NoOnes))"); // using "no" like No one
		patternsList.add("VP=head $, NP=np : (=np < (DT < @NO))"); // ##1
//		patternsList.add("VP=head $,, NP=np : (=np <+(NP) (DT < @NO))");//#4
//		patternsList.add("VP=head $,, @AD=ad : (=ad < (__ < @NO))");
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=ad $,, =vb < (RB < @NO))");
		patternsList.add("VP=head < @AD=ad : (=ad < (__ < @NO))");
		patternsList.add("NP=head < DT=dt : (=dt < @NO)"); //?
		patternsList.add("@AD=head < __=label : (=label < @NO)");
		/** NOT **/
		patternsList.add("VP=head < @RB-NOT < VP=vp : (=vp $,, =rb)");
		patternsList.add("VP=head $, RB=rb > VP=parent : (=rb < @NOT >1 =parent)"); //#a
		patternsList.add("VP=head $,, __=label : (=label <1 @RB-NOT) : (=label <2 (NN < @Anyone))"); // : NOT and ANYONE don't have one parent
		patternsList.add("VP=head $,, RB=rb $,, NP=np : (=rb < @NOT $. =np) : (=np <+(NP) ( NN < @Anyone))"); // like: not anyone likes this movie 
		patternsList.add("VP=head $, NP=np : (=np <+(NP) (@RB-NOT $. (NN < @Anyone)))");
		patternsList.add("VP=head $, NP=np : (=np <+(NP) (RB < @NOT=not)) : (=np !<< (@Only , =not))");
		patternsList.add("VP=head < @VB=vb < RB=rb : (=rb $.. =vb < @NOT)");
		patternsList.add("VP=head $,, __=label : (=label < @NOT=not) : (=not !. @Only)");
		patternsList.add("NP=head <1 __=label : (=label < @NOOT)");
		patternsList.add("NP=head $,, __=label: (=label < @NOOT)");
		patternsList.add("@AD=head $,, __=label : (=label < @NOOT=not) : (=not !. @Only)");
		patternsList.add("PP=head , @NOOT");
		patternsList.add("@AD=head < @RB-NOT");
		patternsList.add("S=head $, __=label: (=label < @NOOT)"); //?
		/** FRAG  **/ 
		patternsList.add("VP=head $, RB=rb : (=rb < @NOT) : (FRAG < =head < =rb)");
		patternsList.add("@AD=head >> FRAG=frag : (=head ,, (RB=rb < @NOT) ?.. (CC < but|But|BUT))"); 
		patternsList.add("@AD=head $,, @RB-NOT : (=head >> FRAG)");
		patternsList.add("NP=head ,, @RB-NOT >> FRAG"); // Fragment structure
		return patternsList;
	}
	
	public   List<String> getAuxiliaryPattern(){
		List<String> patternsList =  new ArrayList<String>();
		patternsList.add("VP=h < @VB < @RB-NOT < VP"); // auxiliary (to be/have/do)
		patternsList.add("VP=h < MD=md < @RB-NOT < VP : (=md < @WILL)"); // will 
		patternsList.add("VP=h < @VB < @RB-NOT < VP=vp : (=vp < VBN < VP)"); // double auxiliary  
		return patternsList;
	}
	
 	private   List<String> getPseudoNegationPattern() {
		List<String> patternsList =  new ArrayList<String>();
		patternsList.add("RB=rb . __=label : (=rb < @NOT) : (=label < @Only)"); // not only ... but also ...
		return patternsList;
	}
	
	public   boolean isPseudoNegation(Tree tree) {
		List<String> patterns = getPseudoNegationPattern();
		TregexPatternCompiler macros=getMacros();
		for (String pattern : patterns) {
			TregexPattern pseudoNegationTregex = macros.compile(pattern);
			TregexMatcher matcher = pseudoNegationTregex.matcher(tree);
			if (matcher.find()) {
				System.out.println("PseuDooooo!");
				return true;
			}
		}
		return false;
	}
	public   boolean containNegationCues(List<HasWord> sentence) {
		List<String> negatedList = Arrays.asList(negatedCues);
		for (HasWord word : sentence) {
			if (negatedList.contains(word.word().toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public boolean isNegatedCue (String word) {
		return Arrays.asList(negatedCues).contains(word.toLowerCase());
	}

	private final   String[] negatedCues = {"NO","No","no","NOT","Not","not", "n't", "n’t", "NEVER", "Never", "never", 
		"BARELY", "Barely", "barely", "SCARCELY", "Scarcely", "scarcely", "HARDLY","Hardly","hardly", "Nothing","NOTHING", "nothing",
		"NEITHER", "Neither", "neither", "NOR", "Nor", "nor", "FEW", "Few", "few", "LITTLE", "Little","little", "NOBODY", "Nobody", "nobody",
		"NONE", "None", "none", "NOWHERE", "Nowhere","nowhere", "WITHOUT", "Without", "without"}; 
}
