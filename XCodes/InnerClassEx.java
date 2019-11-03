public class InnerClassEx {
	String word;
	Properties properties;

	class Properties {
		int count;
		double probability;

		public void make(String word) {
			if (count == 1)
				InnerClassEx.this.word = word;
		}

	}
}
