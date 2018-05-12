import java.awt.Color;

public class Kreeper {

	/**
	 * runs program
	 * 
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// Scanner pauser = new Scanner(System.in);
		Display d = new Display();
		Map m = new Map(20, new Color[] { Color.green, Color.red, Color.blue,Color.white },
				d.getGraphics());
		boolean running = true;
		while (running) {
			m.run(d.input.getMouse());
			running = d.run();
			Thread.sleep(50);
			// pauser.nextLine();
		}
		System.exit(0);
	}

	/**
	 * prints an object with println
	 * 
	 * @param a is object to print
	 */
	private static void print(Object a) {
		System.out.println(a);
	}

	/**
	 * prints each element of list. seperated by commas
	 * 
	 * @param a list of elements to print.
	 */
	private static void print(int[] a) {
		String t = "";
		for (int b : a)
			t += b + ", ";
		print(t);
	}

}
