import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Right {

	public static void main(String[] arg) throws MalformedURLException {
		String test = "http://www.youtubeinmp4.com/redirect.php?video=WzZ_t1nCsR4";
		test = "https://www.youtube.com/watch?v=WzZ_t1nCsR4&list=PLameShrvoeYfzOWuBX2bbER0LXD9EuxGx&index=1";
		URL url = new URL(test);
		openWebpage(url);
	}

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop()
				: null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}