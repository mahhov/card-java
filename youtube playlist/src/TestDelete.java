public class TestDelete {

	static String binary(int n) {
		if (n == 0)
			return "";
		else if (n - n / 2 * 2 == 1)
			return binary(n / 2) + "1";
		else
			return binary(n / 2) + "0";

	}

	static String binaryTail(int n, String result) {
//		System.out.println(n + " " + result);
		if (n == 0)
			return result;
		else if (n - n / 2 * 2 == 1)
			return binaryTail(n / 2, "1" + result);
		else
			return binaryTail(n / 2, "0" + result);

	}

	public static void main(String[] args) {
		int n = 2394;
		System.out.println("binary Tail : " + binaryTail(n, ""));
		System.out.println("binary      : " + binary(n));
	}
}
