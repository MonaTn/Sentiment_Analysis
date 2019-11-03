
	public enum CopyOfTaggTypes {
		INTERJECTION,
		PREPOSITION,
		CONJUCTION,
		PRONOUN,
		ADJECTIVE, 
		ADVERB, 
		NOUN,
		VERB; 
		
		public static CopyOfTaggTypes adaptingTagg (String tagg) {
			if (tagg.contains("JJ"))
				return CopyOfTaggTypes.ADJECTIVE;
			else if (tagg.contains("VB"))
				return CopyOfTaggTypes.VERB; 
			else if (tagg.equals("CC"))
				return CopyOfTaggTypes.CONJUCTION;
			else if (tagg.equals("IN"))
				return CopyOfTaggTypes.PREPOSITION;
			else if (tagg.contains("NN"))
				return CopyOfTaggTypes.NOUN;
			else if (tagg.contains("PR"))
				return CopyOfTaggTypes.PREPOSITION;
			else if (tagg.equals("UH"))
				return CopyOfTaggTypes.INTERJECTION;
			else 
				return null;
			
		}
	}


