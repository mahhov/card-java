import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Engine {
	boolean running;
	Painter painter;

	Engine() {
		painter = new Painter();
	}

	void run() {
		running = true;
		while (running) {
			FrameGrabber grabber = new FrameGrabber();
			if (grabber.init() == true) {
//				BufferedImage frame = grabber.grab();
//				Graphics2D brush = painter.getBrush();
//				brush.drawImage(frame, 0, 0, 800, 800, null);
			}
		}
	}

	public static void main(String[] arg) {
		new Engine().run();
	}
}
