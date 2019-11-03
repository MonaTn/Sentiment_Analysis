package negation;

/**
 * Last modification 16 October 2013
 * Delete few and little patterns
 */
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class NegationPreprocessor {

	public TregexPatternCompiler getMacros() {
		TregexPatternCompiler macros = new TregexPatternCompiler();
		macros.addMacro("@VB", "/^VB/");
		macros.addMacro("@AD", "ADVP|ADJP");
		macros.addMacro("@PRN", "NP|ADJP|ADVP|S|PP");
		macros.addMacro("@SPRN", "NP|ADJP|ADVP|S|PP");
		macros.addMacro("@RB-NOT", "(RB=rb < @NOT)");
		macros.addMacro("@RB-T", "(RB < @T)");
		macros.addMacro("@Neither", "(__ < /^(?i:neither)/)");
		macros.addMacro("@Nor", "(__ < /^(?i:nor)/)");
		macros.addMacro("@Aux", "HAVE|Have|have|HAS|Has|has|HAD|Had|had|'VE|'ve|’VE|’ve|'S|'s|’S|’s|S|s|DO|Do|do|DOES|Does|does|DID|Did|did");
		macros.addMacro("@Be", "AM|Am|am|'M|'m|’M|m|AI|Ai|ai|IS|Is|is|ARE|Are|are|WAS|Was|was|WERE|Were|were|'S|'s|’S|’s|S|s|'RE|'re|’RE|’re");
		macros.addMacro("@WILL", "/^(?i:will)/|/^(?i:wo)/|/^(?i:would)/");
		macros.addMacro("@CAN", "/^(?i:can)/");
		macros.addMacro("@NADV", "never|Never|NEVER|BARELY|Barely|barely|SCARCELY|Scarcely|scarcely|HARDLY|Hardly|hardly");
		macros.addMacro("@Only", "Only|ONLY|only|SOLELY|Solely|solely");
		macros.addMacro("@Nothing", "/^(?i:nothing)/");
		macros.addMacro("@Nowhere", "/^(?i:nowhere)/");
		macros.addMacro("@NoOnes", "One|one|One|Person|person|PERSON");
		macros.addMacro("@Nobody", "/^(?i:nobody)/");
		macros.addMacro("@Anybody", "/^(?i:anybody)/");
		macros.addMacro("@Anyone", "/^(?i:anyone)/");
		macros.addMacro("@Lack", "LACK|Lack|lack|LACKS|Lacks|lacks|LACKED|Lacked|lacked|LACKING|Lacking|lacking");
		macros.addMacro("@None", "None|NONE|none");
		macros.addMacro("@NOT", "NOT|Not|not|n’t|n't|N'T|N’T|n't");
		macros.addMacro("@NOOT", "NOT|Not|not");
		macros.addMacro("@NO", "NO|No|no");
		macros.addMacro("@A", "(DT < /[Aa]/)");
		return macros;
	}

	public List<String> getPatternsList() {
		List<String> patternsList =  new ArrayList<String>();
		/** Without  1 pattern **/
		patternsList.add("PP=head < IN=in : (=in < /^(?i:without)$/)");
		/** Nothing 7 patterns **/
		patternsList.add("S=head < NP=np < VP=vp : (=np <+(__) @Nothing $. =vp) : (=vp !< VP)");
		patternsList.add("VP=head , NP=np : (=np <+(NP) NN=nn) : (=nn < @Nothing)"); // in subject * [<< <+(NP)]
		patternsList.add("VP=head < @VB=vb < NP=np : (=np <+(NP) (NN < @Nothing))");
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=ad ,, =vb < (NN < @Nothing))");
		patternsList.add("VP=head < @VB=vb < @PRN=prn : (=prn << NN=nn) : (=nn < @Nothing) : (=nn !>> PP)"); // in object
		patternsList.add("NP=head < __=label : (=label < @Nothing)"); //?
		/** patternsList.add("VP=head , @Nothing"); **/
		patternsList.add("VP=head ,+(NP) @AD=ad : (=ad < __=label) : (=label < @Nothing)");
		/** Nobody 2 pattern **/
		patternsList.add("VP=head , NP=np : (=np <+(NP) NN=nn) : (=nn < @Nobody)"); // in subject
		patternsList.add("VP=head < @PRN-SBAR=prn : (=prn << @Nobody)"); // in object
		/** None of 3 patterns **/
		patternsList.add("VP=head $,, NP=np : (=np <+(NP) __=label) : ( =label < @None=none) : (=none ?. of|Of|OF)"); // in subject
		patternsList.add("VP=head < NP=np : (=np <+(NP) __=label) : (=label < (@None ?. of|Of|OF))"); // in object
		patternsList.add("NP=head << NP=np << PP=pp : (=np <+(NP) __=label) : (=label < @None) : (=pp << of|Of|OF) : (=np $. =pp)");
		patternsList.add("@AD=head < NP=np : (=np <+(NN) @None)");
		/** Nowhere 2 patterns **/
		patternsList.add("VP=head < @VB < @AD=ad : (=ad << RB=rb) : (=rb < @Nowhere)"); // as adverb
		patternsList.add("VP=head < @VB < NP=np : (=np << JJ=adj) : (=adj < @Nowhere)"); // as adjective
		/** Aux (have/do) not VP 2 patterns **/
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Aux $. @RB-NOT)");
//		patternsList.add("VP=head $, TO : (=head < @RB-NOT)");
		patternsList.add("VP=head < @VB=vb < VP=vp : (=vb < @Aux .. RB=rb) : (=rb < @NOT=not !> PP) : (=not >> =head) : (=vp < @VB)"); // !>> S)"); // VP-aux (have/do) not VP
		/** TO Be not PRD/VP 6 patterns **/
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Be $. @RB-NOT)"); // VP-be not VP => Be is aux verb
		patternsList.add("VP=head < @VB=vb < RB=rb : (=vb $.. =rb) : (=rb < @NOT) : (=rb !$,, CC)");
		patternsList.add("VP=head < @VB=vb < @PRN : (=vb < @Be $. @RB-NOT)"); // VP-be not PRD => Be is main verb
		patternsList.add("VP=head < @VB=vb < @PRN : (=vb < @Be=be .. RB=rb) : (=rb < @NOT ,, =be ) : (=rb > =head)");
		patternsList.add("VP=head < @VB=vb < RB=rb < VP : (=vb < @Be .. (=rb < @NOT)) "); // VP-be not VP => Be is aux verb
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=vb < @Be $. =ad) : (=ad < @RB-NOT)");
		/** Modal 4 patterns **/
		patternsList.add("VP=head < MD=md < VP : (=md $. @RB-NOT)");
		patternsList.add("VP=head < MD=md < VP : (=md $.. @AD=ad) : (=ad < @RB-NOT)");
		patternsList.add("VP=head < MD=md < VP : (=md $. @AD=ad) : (=ad $. @RB-NOT)");
		patternsList.add("VP=head < MD=md < VP : (=md < @CAN $. @AD=ad) : (=ad < @RB-T)");
		/** Short answer and elliptic sentences 2 patterns **/
		patternsList.add("VP=head < @VB=vb [<- @RB-NOT | <- @Neither] : (=vb < @Be|@Aux)");
		patternsList.add("VP=head < MD [<- @RB-NOT | <- @Neither]");
		/** Negative adverb of frequency 6 patterns **/
		patternsList.add("VP=head < MD < @AD=ad : (=ad < RB=rb) : (=rb < @NADV)");
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=vb < @Be) : (=ad < RB=rb ,, =vb) : (=rb < @NADV)"); // with to be
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=vb $, =ad) : (=ad << @NADV)");
		patternsList.add("VP=head < @VB < @AD=ad : (=ad < RB=rb) :  (=rb < @NADV)");
		patternsList.add("VP=head $,, @AD=ad : (=ad < RB=rb) : (=rb < @NADV)");
		patternsList.add("VP=head > S=s : (=s $, @AD=ad) : (=ad < RB=rb) : (=rb < @NADV)");
		patternsList.add("@AD=head < @AD=ad : (=ad <+(RB) @NADV)");
		/** No 7 patterns**/
		patternsList.add("S=head < NP=np < VP=vp : (=np >, =head <+(__) @NO) : (=vp !< VP)");//*
		patternsList.add("S=head < INTJ=intj < VP=vp : (=intj >, =head <+(__) @NO) : (=vp !< VP)");//*
		patternsList.add("VP=head <@VB=vb < NP=np : (=vb $. (=np < DT=dt)) : (@NO >, =dt)");
		patternsList.add("VP=head $,, NP=np : (=np <1 (DT < @NO)) : (=np <2 (NN < @NoOnes))"); // using "no" like No one
		patternsList.add("VP=head $, NP=np : (=np < (DT=dt < @NO)) : (=dt [!.. /,/ & !.. /:/]) : (=head !< CC)"); // ##1
		patternsList.add("VP=head < NP=np : (=np <+(NP) (DT < @NO))"); // using "no" after verb as negation *
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=ad $,, =vb < (RB < @NO))");
		patternsList.add("VP=head < @VB < VP=vp : (=vp < @AD=ad) : (=ad <+(RB) @NO)");
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=vb < @Be) : (=ad << @NO)");
		patternsList.add("VP=head < @AD=ad : (=ad < (__ < @NO))");
		patternsList.add("VP=head < @VB=vb < @AD=ad < @VP=vp : (=ad $, =vb $. =vp << @NO) : (=vb [< @Be| < @Aux])");
		patternsList.add("VP=head $, @AD=ad : (=ad < (__ < @NO))");
		patternsList.add("NP=head < DT=dt : (=dt < @NO >, =head)"); //?
		patternsList.add("@AD=head < __=label : (=label < @NO)");
		patternsList.add("INTJ=head < UH=uh : (=uh < @NO)");
		/** NOT 14 patterns**/
		patternsList.add("S=head <<: VP=vp , RB=rb: (=vp <1 TO) : (=rb < @NOT)");
		patternsList.add("VP=head < RB=rb < VP=vp ?< CC=cc : (=rb < @NOT) : (=cc < /^(?i:and)$/ !$. =rb) : (=vp $,, =rb)");
		patternsList.add("VP=head $, RB=rb > VP=parent : (=rb < @NOT >1 =parent)"); //#a
		patternsList.add("VP=head $,, __=label : (=label <1 @RB-NOT) : (=label <2 (NN < @Anyone))"); // : NOT and ANYONE don't have one parent
		patternsList.add("VP=head $,, RB=rb $,, NP=np : (=rb < @NOT $. =np) : (=np <+(NP) ( NN < @Anyone))"); // like: not anyone likes this movie
		patternsList.add("VP=head $, NP=np : (=np <+(NP) (@RB-NOT $. (NN < @Anyone)))");
		patternsList.add("VP=head $, NP=np : (=np <+(NP) (RB < @NOT=not)) : (=np !<< (@Only , =not))");
		patternsList.add("VP=head < @VB=vb < RB=rb : (=rb $.. =vb < @NOT)");
		patternsList.add("VP=head $,, __=label : (=label < @NOT=not) : (=not !. @Only)");
		patternsList.add("S=head $, WHADVP=wh : (=wh < (RB=rb < @NOT)) : (=rb >- =wh)");
		patternsList.add("NP=head < __=label : (=label < @NOOT)");
		patternsList.add("NP=head $,, __=label: (=label < @NOOT)");
		patternsList.add("@AD=head $,, __=label : (=label < @NOOT=not) : (=not !. @Only)");
//		patternsList.add("PP=head , @NOOT");
		patternsList.add("@AD=head < RB=rb ?< CC=cc : (=rb < @NOT) : (=cc < /^(?i:and)$/ !$. =rb)");
		patternsList.add("S=head $, __=label: (=label < @NOOT)"); //?
		patternsList.add("FRAG=head < @AD=ad < RB=rb : (=ad >, =head) : (=rb >2 =head)");
		patternsList.add("SBAR=head $, RB=rb : (=rb < @NOOT)");
		/** Neither Nor structure 9 patterns **/
		patternsList.add("VP=head $, NP=np : (=np << @Neither << @Nor)");// Neither & nor structure by NP
		patternsList.add("VP=head $, @AD=ad : (=ad << @Neither !<< @Nor)");// Neither & nor structure by ADVP
		patternsList.add("VP=head < @AD=ad : (=ad << @Neither << @Nor)"); //new , VP < ADJP #1
		patternsList.add("VP=head < @VB <+(S) NP=np : (=np << @Neither << @Nor)"); //NEW, NEITHER & NOR INSIDE OF VP BY NP #2
		patternsList.add("VP=head < @VB < @SPRN=prn : (=prn << @Neither ?<< @Nor)");// Neither & nor structure INSTEAD OF #1 & #2
		patternsList.add("VP=head $, NP=np !< @Nor: (=np << @Neither !<<@Nor)");// Neither structure
		patternsList.add("VP=head , @Nor"); // Nor structure ???? example
		patternsList.add("NP=head < @Neither ?<@Nor");
		patternsList.add("NP=head < @Nor !< @Neither"); //MODIFY
		/** FRAG 5 patterns **/
		patternsList.add("VP=head $, RB=rb : (=rb < @NOT) : (FRAG < =head < =rb !$, WHADVP)");
//		patternsList.add("@AD=head >> FRAG=frag : (=head $,, (RB=rb < @NOT) ?.. (CC < but|But|BUT)) : (=frag !$, WHADVP)");
		patternsList.add("@AD=head $,, RB=rb : (=rb < @NOOT) : (=head >> FRAG=frag) : (=frag !$, WHADVP)");
		patternsList.add("CONJP=head < __=label : (=label < @Not)"); // Conjunction Phrase
		patternsList.add("NP=head $,, @RB-NOT >> FRAG=frag : (=frag !$, WHADVP)"); // Fragment structure
		patternsList.add("FRAG=head < RB=rb : (=rb < @NOOT)");
		/** Lack **/
		patternsList.add("NP=head < JJ=jj : (=jj < @Lack)");
		patternsList.add("PP=head $, NP=np > NP=parent : (=np <+(NN) @Lack > =parent) : (=head <+(IN) /^(?i:of)$/)");
		patternsList.add("PP=head $, NP=np : (=np <+(NN) @Lack) : (=head <+(IN) /^(?i:of)$/)");
		patternsList.add("NP=head < NP=np < PP=pp : (=np <+(NN) @Lack $. =pp) : (=pp <+(IN) /^(?i:of)$/)");
		patternsList.add("VP=head < @VB=vb < NP : (=vb < @Lack)");
		patternsList.add("NP=head < NN=nn : (=nn < @Lack=lack ) : (=lack !>: =nn)");
		patternsList.add("NP=head <+(NP) NN=nn < NP : (=nn < @Lack=lack >>, =head)");

		patternsList.add("NP-TMP=head $, RB=rb : (=rb [< @NOT | < @NO])");

		return patternsList;
	}

	public boolean isPseudoNegation(Tree tree) {
		List<String> patterns = getPseudoNegationPattern();
		TregexPatternCompiler macros=getMacros();
		for (String pattern : patterns) {
			TregexPattern pseudoNegationTregex = macros.compile(pattern);
			TregexMatcher matcher = pseudoNegationTregex.matcher(tree);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}

 	private List<String> getPseudoNegationPattern() {
		List<String> patternsList =  new ArrayList<String>();
		patternsList.add("RB=rb . __=label : (=rb < @NOT) : (=label < @Only)"); // not only ... but also ...
		return patternsList;
	}

	public boolean containNegationCues(List<HasWord> sentence) {
		for (HasWord word : sentence) {
			if ( NegationCues.isCues(word.word().toLowerCase())) {
				return true;
			}
		}
		return false;
	}



}


