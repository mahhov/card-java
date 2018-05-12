package all;
import java.io.*;
import java.net.*;

//can call php script with post request
public class UrlWriter {

	private URL url;
	private HttpURLConnection connection;

	// initialize
	UrlWriter(String url) {
		try {
			this.url = new URL(url);
		} catch (IOException e) {
			System.out
					.println("can't open url for sending to php, poop code10");
			e.printStackTrace();
		}
	}

	// connects (must be called every time by sendRequst)
	private void makeConnection() {
		try {
			connection = null;
			connection = (HttpURLConnection) this.url.openConnection();
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		} catch (IOException e) {
			System.out.println("can't create connection with php, poop code11");
			e.printStackTrace();
		}
	}

	// calls php
	void sendRequest(String pass, boolean writeOutput) {
		try {
			// write request
			makeConnection();
			DataOutputStream o = new DataOutputStream(
					connection.getOutputStream());
			o.writeBytes(pass);
			o.flush();
			o.close();

			// get response
			// InputStream is necessary even if not read or else writing
			// doesn't work after
			InputStream i = connection.getInputStream();
			if (writeOutput) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(i));
				String line;
				String response = "";
				while ((line = rd.readLine()) != null)
					response += line + "\n";
				rd.close();
				System.out.println(response);
			}
			i.close();

		} catch (IOException e) {
			System.out.println("can't write to php, poop code12");
			e.printStackTrace();
		}
	}

	// unit test code
	public static void main(String[] args) {
		UrlWriter php = new UrlWriter(
				"http://10.142.32.75/write.php");
		php.sendRequest("name=jASava", true);
	}
}

// EXAMPLE
// UrlWriter php = new UrlWriter(
// "http://mhlaptop.gateway.pace.com/write.php");
// php.sendRequest("name=java", true);