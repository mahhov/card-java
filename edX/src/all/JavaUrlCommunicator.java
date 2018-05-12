package all;

// what should be running on student computer
public class JavaUrlCommunicator implements Runnable {

	private UrlReader reader;
	private UrlWriter writer;

	private StringQueue writeBuffer, readBuffer;

	private boolean stop;

	// initialize
	// dataSource is xml we read data from
	// writePhp is php script we send data to
	JavaUrlCommunicator(String dataSource, String writePhp) {
		reader = new UrlReader(dataSource);
		writer = new UrlWriter(writePhp);
		writeBuffer = new StringQueue(10);
		readBuffer = new StringQueue(10);
	}

	public void addWriteData(String data) {
		writeBuffer.add(data);
	}

	public String[] getReadData(String data) {
		return readBuffer.removeAll();
	}

	public void stop() {
		stop = true;
	}

	public void run() {
		while (!stop) {
			String s;
			while ((s = writeBuffer.remove()) != null) {
				String request = "status=" + s;
				writer.sendRequest(request, false);
			}
			readBuffer.add(reader.read());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// unit test code
	public static void main(String[] args) {
		// String address = "http://192.168.1.64:80/";
		String address = "http://inst.eecs.berkeley.edu/~cs184-ce/edx/";
		JavaUrlCommunicator c = new JavaUrlCommunicator(address + "data.xml", address
				+ "write.php");
		Thread t = new Thread(c);
		t.start();
		c.addWriteData("hello");
		System.out.println("sent hello");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.addWriteData("my name is java");
		System.out.println("sent java");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.addWriteData("bye");
		System.out.println("sent bye");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.stop();
	}
}

// EXAMPLE
// JavaUrlCommunicator c = new JavaUrlCommunicator(
// "http://mhlaptop.gateway.pace.com/data.xml",
// "http://mhlaptop.gateway.pace.com/write.php");
// Thread t = new Thread(c);
// t.start();
// c.addWriteData("hello");
// System.out.println("sent hello");
// try {
// Thread.sleep(5000);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// c.addWriteData("my name is java");
// System.out.println("sent java");
// try {
// Thread.sleep(5000);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// c.addWriteData("bye");
// System.out.println("sent bye");
// try {
// Thread.sleep(5000);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// c.stop();
