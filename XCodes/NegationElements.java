package Negation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

public class NegationElements {
	
	public static TregexPatternCompiler getMacros() {
		TregexPatternCompiler macros = new TregexPatternCompiler();
		macros.addMacro("@Be", "am|is|are|was|were|'s|'re");
		macros.addMacro("@Aux", "have|has|had|'ve|'s|do|does|did");
		macros.addMacro("@NADV", "never|barely|scarcely|hardly");
		macros.addMacro("@Few", "/[Ff]ew/");
		macros.addMacro("@Little", "/[Ll]ittle/");
		macros.addMacro("@Nothing", "/[Nn]othing/");
		macros.addMacro("@Nowhere", "/[Nn]owhere/");
		macros.addMacro("@Nobody", "/[Nn]obody/");
		macros.addMacro("@Anybody", "/[Aa]nybody/");
		macros.addMacro("@None", "/[Nn]one/");
		macros.addMacro("@PRN", "NP|ADJP|ADVP|PP|S|SBAR");
		macros.addMacro("@PRN-SBAR", "NP|ADJP|ADVP|S|PP");
		macros.addMacro("@VB", "/^VB/");
		macros.addMacro("@AD", "ADVP|ADJP");
		macros.addMacro("@WILL", "will|wo|would");
		macros.addMacro("@A", "(DT < /[Aa]/)");
		macros.addMacro("@RB-NOT", "(RB < /[Nn]['o]t/)");
		macros.addMacro("@Neither", "(__ < /[Nn]either/)");
		macros.addMacro("@NOT", "/[Nn][Oo][Tt] /");
		macros.addMacro("@Nor", "(__ < /[Nn]or /)");
		return macros;
	}

	public static List<String> getPatternsList() {
		List<String> patternsList =  new ArrayList<String>();
		//Fragment structure 
		patternsList.add("@AD=head >> FRAG : (=head ,, @RB-NOT ?.. (CC < but))"); 
		patternsList.add("NP=head ,, @RB-NOT >> FRAG"); // Fragment structure
		// Neither structure 
		patternsList.add("VP=head , @Neither !< @Nor"); // Neither structure 
		patternsList.add("VP=head , @Nor"); // Nor structure 
		patternsList.add("VP=head , NP=np : (=np << @Neither ?<< @Nor)");// Neither & nor structure 
		patternsList.add("VP=head < @PRN-SBAR=prn : (=prn << @Neither ?<< @Nor)");// Neither & nor structure 
		patternsList.add("VP=head < @VB [<< @Neither | << @Nor]"); // Using neither & nor structure with to be 
		// Short answer
		patternsList.add("VP=head < @VB=vb [<- @RB-NOT | <- @Neither] : (=vb < @Be|@Aux|MD)"); 
		// VP-aux (have/do) not VP
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Aux $+ @RB-NOT)"); 
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Aux .. @RB-NOT)"); // VP-aux (have/do) not VP
		// VP-be not PRD/VP
		patternsList.add("VP=head < @VB=vb < @PRN : (=vb < @Be $+ @RB-NOT)"); // VP-be not PRD => Be is main verb 
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Be $+ @RB-NOT)"); // VP-be not VP => Be is aux verb
		patternsList.add("VP=head < @VB=vb < VP : (=vb < @Be .. @RB-NOT)"); // VP-be not VP => Be is aux verb
		// VP-modal not VP 
		patternsList.add("VP=head < MD=md < VP : (=md $+ @RB-NOT)"); 
		// No
		patternsList.add("VP=head < NP=np : (=np << (DT < no))"); // using "no" after verb as negation *
		patternsList.add("VP=head $,, NP=np : (=np < (DT < /[Nn]o/))"); // using "no" like No one
		// NOT
		patternsList.add("VP=head $,, @RB-NOT");
		patternsList.add("NP=head < __=label : (=label < @NOT)");
		// Using negative adverb of frequency 
		patternsList.add("VP=head $,, @AD=ad : (=ad < RB=rb) : (=rb < @NADV)"); 
		patternsList.add("VP=head < @VB=vb < @AD=ad : (=vb < @Be) : (=ad < RB=rb ,, =vb) : (=rb < @NADV)"); // with to be
		// Using few 
		patternsList.add("VP=head << NP=np !<< SBAR : (=np << JJ=adj) : (=adj < @Few !$, @A)"); // in object
		patternsList.add("VP=head $, NP=np !<< SBAR : (=np << JJ=adj) : (=adj < @Few !$, @A) "); // in subject
		// Using little
		patternsList.add("VP=head << @AD=ad !<< SBAR : (=ad << RB=rb) : (=rb < @Little !$, @A)"); // in object
		patternsList.add("VP=head $, @AD=ad !<< SBAR : (=ad << RB=rb) : (=rb < @Little !$, @A)"); // in subject
		patternsList.add("VP=head << NP=np !<< SBAR : (=np << JJ=adj) : (=adj < @Little !$, @A)"); // in object
		patternsList.add("VP=head $, NP=np !<< SBAR : (=np << JJ=adj) : (=adj < @Little !$, @A)"); // in subject
		// Nothing
		patternsList.add("VP=head , NP=np : (=np << NN=nn) : (=nn < @Nothing)"); // in subject
		patternsList.add("VP=head < @VB=vb < @PRN-SBAR=prn : (=prn << NN=nn) : (=nn < @Nothing)"); // in object
		// Nobody
		patternsList.add("VP=head , NP=np : (=np << @Nobody)"); // in subject
		patternsList.add("VP=head < @PRN-SBAR=prn : (=prn << @Nobody)"); // in object
		// Using "none of"
		patternsList.add("VP=head $,, NP=np : (=np << (@None ?. of))"); // in subject 
		patternsList.add("VP=head << NP=np : (=np << (@None ?. of))"); // in object
		// Nowhere
		patternsList.add("VP=head < @VB < @AD=ad : (=ad << RB=rb) : (=rb < @Nowhere)"); // as adverb
		patternsList.add("VP=head < @VB < NP=np : (=np << JJ=adj) : (=adj < @Nowhere)"); // as adjective
		return patternsList;
	}
	
	public static List<String> getAuxiliaryPattern(){
		List<String> patternsList =  new ArrayList<String>();
		patternsList.add("VP=h < @VB < @RB-NOT < VP"); // auxiliary (to be/have/do)
		patternsList.add("VP=h < MD=md < @RB-NOT < VP : (=md < @WILL)"); // will 
		patternsList.add("VP=h < @VB < @RB-NOT < VP=vp : (=vp < VBN < VP)"); // double auxiliary  
		return patternsList;
	}
	
 	private static List<String> getPseudoNegationPattern() {
		List<String> patternsList =  new ArrayList<String>();
		patternsList.add("RB=notRB . RB=rb : (=notRB < not) : (=rb < only|solely)"); // not only ... but also ...
		return patternsList;
	}
	
	public static boolean isPseudoNegation(Tree tree) {
		List<String> patterns = getPseudoNegationPattern();
		for (String pattern : patterns) {
			TregexPattern pseudoNegationTregex = TregexPattern.compile(pattern);
			TregexMatcher matcher = pseudoNegationTregex.matcher(tree);
			if (matcher.find()) {
				System.out.println("PseuDooooo!");
				return true;
			}
		}
		return false;
	}
	public static boolean isNegated(List<HasWord> sentence) {
		List<String> negatedList = Arrays.asList(negatedWords);
		for (HasWord word : sentence) {
			if (negatedList.contains(word.word().toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	private final static String[] negatedWords = {"no","not", "n't", "never", "barely","scarcely","hardly", "neither","nor", "few", "little", "nothing", "nobody", "none", "nowhere"}; 
}

//patternsList.add("VP=head , NP=np : (=np << @Anybody)"); // Anybody in subject
//patternsList.add("VP=head < @PRN-SBAR=prn : (=prn << @Anybody)"); // Anybody in object