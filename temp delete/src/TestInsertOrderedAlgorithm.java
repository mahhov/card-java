public class TestInsertOrderedAlgorithm {

	static int[] a;
	static int slavesAdded = 0;

	public static void main(String[] args) {
		a = new int[10];
		insert(3);
		insert(6);
		insert(9);
		insert(2);
		insert(4);
		insert(8);
		insert(-1);
		insert(4);
		print(a);
	}

	private static void insert(int x) {
		for (int i = 0; i < slavesAdded; i++) {
			if (a[i] > x) {
				for (int j = slavesAdded; j > i; j--)
					a[j] = a[j - 1];
				a[i] = x;
				slavesAdded++;
				return;
			}
		}
		a[slavesAdded++] = x;
	}

	static void print(int ss[]) {
		for (int s : ss)
			System.out.print(s + ", ");
		System.out.println("");
	}

}
