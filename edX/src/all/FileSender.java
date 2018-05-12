package all;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// constantly checks and sends file content to server

public class FileSender implements Runnable {
	// what should be running on student computer

	private String file;
	private UrlWriter writer;

	private boolean stop;

	// initialize
	// file is file we will be sending
	// writePhp is php script we send data to
	FileSender(String file, String writePhp) {
		this.file = file;
		writer = new UrlWriter(writePhp);
	}

	public void stop() {
		stop = true;
	}

	public void run() {
		while (!stop) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line1 = br.readLine();
				// ... read rest of lines that we expect in file format
				br.close();

				String request = "status=" + line1;
				writer.sendRequest(request, false);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// unit test code
	public static void main(String[] args) {
		FileSender fs = new FileSender("send.txt",
				"http://inst.eecs.berkeley.edu/~cs184-ce/edx/write.php");
		Thread t = new Thread(fs);
		t.start();
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fs.stop();
	}
}
