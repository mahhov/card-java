package all;
import java.io.*;
import java.util.*;

/**
 * Checker is the main class of the application. The main() method in Checker
 * repeatedly opens the file given by FILENAME, reads the first line, checks it
 * against the checkpoints, and progresses through the lab. The checkpoints are
 * listed by name in the file given by args[0]. Every line in that file should
 * correspond to a .txt file of the same name in which each line is
 * "<analog 1 reading>_ <analog 2 reading>_ <message to give>". For example, if
 * a reading of 2V and 5.5V on analog 1 and 2 signifies that the op amp is
 * reversed, that line might read: 2_ 5.5_ op amp reversed Every checkpoint file
 * must contain one condition whose message is "done", so that the lab can
 * progress.
 * 
 * @author Kushal Ranjan
 */
public class Checker {

	static final String FILENAME = "measurements.txt"; // Location of the
														// measurements
	static final double TOLERANCE = 0.05; // 5% tolerance on measurements
	static final int DELAY = 500; // Delay between measurements, in ms

	public static void main(String[] args) {
		UrlWriter php = new UrlWriter("http://10.142.32.75/write.php");

		Iterator<Checkpoint> checkpoints = readCheckpoints(args[0]).iterator();
		RandomAccessFile in = null;

		try {
			in = new RandomAccessFile(FILENAME, "r");
			String raw;
			Checkpoint curr = checkpoints.next();
			String status;
			while ((raw = in.readLine()) != null) {
				// Parse measurement; expect format 'A,B' for analog A and
				// analog B
				String[] inputs = raw.split(",");
				status = curr.getStatus(Double.parseDouble(inputs[0]),
						Double.parseDouble(inputs[1]));
				System.out.println(curr.name + " - " + status);
				if (status.equals("done")) { // Move on to next checkpoint
					curr = checkpoints.next();
				}

				php.sendRequest("status=" + status, true);
				php.sendRequest("part=" + curr.name, true);

				Thread.sleep(DELAY);
				in.seek(0); // Set file reader back to start of file
			}
		} catch (FileNotFoundException e) {
			System.err.println(FILENAME + " not found.");
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("Error reading file: " + FILENAME);
			System.exit(-1);
		} catch (InterruptedException e) {
			System.err.println("Timing error.");
			System.exit(-2);
		} catch (NoSuchElementException e) { // End of checkpoints
			System.out.println("Lab complete.");
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				System.err.println("Error freeing resources.");
				System.exit(-1);
			}
		}
	}

	/**
	 * Parses a file and constructs a list of Checkpoints for this lab. Each
	 * line should correspond to a file (i.e. "part1" in the file means there
	 * should be a "part1.txt") that contains the possible scenarios for each
	 * checkpoint as described above.
	 * 
	 * @param filename
	 *            name of file containing checkpoint list
	 * @return a list of Checkpoint objects
	 */
	static LinkedList<Checkpoint> readCheckpoints(String filename) {
		BufferedReader in = null;
		LinkedList<Checkpoint> checkpoints = new LinkedList<Checkpoint>();
		try {
			in = new BufferedReader(new FileReader(new File(filename)));
			String raw;
			while ((raw = in.readLine()) != null) {
				checkpoints.add(new Checkpoint(raw));
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println(filename + " not found.");
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("Error reading file " + filename);
			System.exit(-1);
		}
		return checkpoints;
	}
}

/**
 * The Checkpoint class represents the multiple checkpoints throughout the lab.
 * Each checkpoint has multiple scenarios, and when the getStatus method is
 * invoked, the correct scenario for the given measurements is determined and
 * returned.
 */
class Checkpoint {
	private double analogA, analogB;
	private LinkedList<Measurement> possibilities; // Possible scenarios
	public String name;

	public Checkpoint(String name) {
		this.name = name;
		possibilities = new LinkedList<Measurement>();
		buildMeasurementList(name + ".txt");
	}

	/**
	 * Builds the list of possible scenarios based on a file. Each line in the
	 * file should be formatted as described above. Note that each file must
	 * contain a "done" condition.
	 * 
	 * @param filename
	 *            name of the file containing the possible scenarios
	 */
	void buildMeasurementList(String filename) {
		String raw;
		boolean doneFound = false;
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(
					filename)));
			while ((raw = in.readLine()) != null) {
				String[] input = raw.split("_ "); // Arbitrary; can change if
													// desired
				Measurement newMeas = new Measurement(
						Double.parseDouble(input[0]),
						Double.parseDouble(input[1]), input[2]);
				if (newMeas.message.equals("done")) {
					doneFound = true;
					analogA = newMeas.analogA;
					analogB = newMeas.analogB;
				}
				possibilities.add(newMeas);
			}
			in.close();
			if (!doneFound) { // No "done" condition
				System.err.println("Checkpoint " + name
						+ " does not have a 'done' condition.");
				System.exit(-3);
			}
		} catch (FileNotFoundException e) {
			System.err.println(filename + " not found.");
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("Error reading file: " + filename);
			System.exit(-1);
		}
	}

	/**
	 * Checks a pair of values against the possible scenarios and returns the
	 * message corresponding to the correct one.
	 * 
	 * @param measA
	 *            measurement from analog 1
	 * @param measB
	 *            measurement from analog 2
	 * @return the correct scenario given the reading (measA,measB)
	 */
	String getStatus(double measA, double measB) {
		System.out.println("analog 1 = " + measA + ", analog 2 = " + measB);
		// Linear in number of measurements; try to find way to make faster
		for (Measurement m : possibilities) {
			if (m.verifyMeasurement(measA, measB))
				return m.message; // "done" signifies moving to next step
		}
		return "status unknown";
	}

	public String toString() {
		return "Checkpoint " + name + ": need V1 == " + analogA
				+ "V and V2 == " + analogB + "V to advance.";
	}
}

/**
 * The Measurement class defines a single possible scenario for the board. The
 * verifyMeasurement() method checks measurements against its parameters and
 * determines whether this is the current scenario.
 */
class Measurement {

	double analogA, analogB;
	String message; // Message to return

	Measurement(double analogA, double analogB, String message) {
		this.analogA = analogA;
		this.analogB = analogB;
		this.message = message;
	}

	/**
	 * Given two measurements, returns whether this scenario is the current
	 * situation.
	 * 
	 * @param measA
	 *            measurement from analog 1
	 * @param measB
	 *            measurement from analog 2
	 * @return true iff measA and measB are within the specified percentage of
	 *         the required values
	 */
	boolean verifyMeasurement(double measA, double measB) {
		double checkA = (analogA == 0) ? measA : measA / analogA - 1;
		double checkB = (analogB == 0) ? measB : measB / analogB - 1;
		return Math.abs(checkA) < Checker.TOLERANCE
				&& Math.abs(checkB) < Checker.TOLERANCE;
	}

	public String toString() {
		return "V1 == " + analogA + "V and V2 == " + analogB + " --> "
				+ message;
	}
}