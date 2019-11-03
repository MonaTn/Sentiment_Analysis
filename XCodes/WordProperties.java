package shared;

public class WordProperties {
	 String tagg;
	 int count;
	 double score;
	 
	 public WordProperties () {}
	 public WordProperties (String tagg, int count, double score) {
		 this.tagg = tagg;
		 this.count = count;
		 this.score = score;
	 }
	 
	public String getTagg() {
		return tagg;
	}
	public void setTagg(String tagg) {
		this.tagg = tagg;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

}
