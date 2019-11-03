import java.lang.Integer;

@SuppressWarnings("hiding")
public class Pair< Integer , Double > {
    private  Integer count; 
    private  Double score ; 

    public Pair () {
    }
    
    public Pair(Integer count, Double score) {
        this.count = count;
        this.score = score;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getCount() {
        return count;
    }

    public Double getScore() {
        return score;
    }
//     public Integer increasCount () {
//     	 int countValue = ((java.lang.Integer) count).intValue()+1;
//    	 return countValue;
//     }
}