package xExtraClass;

public class Stringtest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "te~st";
		str = str.replaceFirst("~", "");
		System.out.println(str);
	}

}
