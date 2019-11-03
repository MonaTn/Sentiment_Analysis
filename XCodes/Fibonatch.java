package xExtraClass;

public class Fibonatch {

	public static void main(String[] args) {
		int n =7;
		System.out.println (fibo(n));
	}

	private static int fibo(int n) {
		return (n == 1) ? 1 : n*fibo(n-1);
	}

}
