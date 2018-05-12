package all;

import java.io.*;
import java.net.*;

//reads file on server
public class UrlReader {

	private URL url;
	private URLConnection connection;
	private BufferedReader i;

	// initialize
	UrlReader(String url) {
		try {
			this.url = new URL(url);
			connection = this.url.openConnection();
			System.out.println("0" + connection);
			InputStream is = connection.getInputStream();
			System.out.println("1");
			InputStreamReader isr = new InputStreamReader(is);
			System.out.println("2");
			i = new BufferedReader(isr);
			System.out.println("3");
		} catch (IOException e) {
			System.out.println("can't read file off server, poop code01");
			e.printStackTrace();
		}
	}

	// close neatly
	void close() {
		try {
			i.close();
		} catch (IOException e) {
			System.out.println("server file reader not closed, poop code02");
			e.printStackTrace();
		}
	}

	// read lines from url
	String read() {
		String input = "";
		String inputLine;
		try {
			connection = url.openConnection();
			i = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			while ((inputLine = i.readLine()) != null)
				input += inputLine;
		} catch (IOException e) {
			System.out.println("can't read file from server, poop code03");
			e.printStackTrace();
		}
		return input;
	}

	// unit test code
	public static void main(String[] args) {
		UrlReader input = new UrlReader("http://inst.eecs.berkeley.edu/~cs184-ce/edx/data.xml");
		System.out.println(input.read());
		input.close();
	}

}

// SIMPLE READ URL
// UrlReader input = new UrlReader("http://mhlaptop.gateway.pace.com/data.xml");
// System.out.println(input.read());
// input.close();