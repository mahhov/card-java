import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;

public class Center {

	public static void main(String arg[]) throws IOException,
			InterruptedException {
		// String playlist =
		// "https://www.youtube.com/playlist?list=PLameShrvoeYfzOWuBX2bbER0LXD9EuxGx";
		// String redirect =
		// "http://www.youtubeinmp4.com/redirect.php?video=WzZ_t1nCsR4";
		// String test =
		// "http://www.youtubeinmp4.com/redirect.php?video=WzZ_t1nCsR4";

		String test = "http://www.youtubeinmp4.com/redirect.php?video=WzZ_t1nCsR4";

		// URL website = new URL(test);
		// ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		// FileOutputStream fos = new FileOutputStream("test.mp4");
		// fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		// Thread.sleep(10000);

		URL url = new URL(test);
		BufferedInputStream in = new BufferedInputStream(url.openStream());
		java.io.FileOutputStream fos = new java.io.FileOutputStream("test.mp4");
		java.io.BufferedOutputStream bout = new BufferedOutputStream(fos);
		byte data[] = new byte[1024];
		int read;
		while ((read = in.read(data, 0, 1024)) >= 0) {
			bout.write(data, 0, read);
		}
		bout.close();
		in.close();
	}
}
