package world;

import java.awt.Color;
import java.awt.Graphics2D;

import network.Client;
import draw.Painter;

public class Guest {

	String ip = "192.168.56.1";

	Client client;
	Painter painter;
	Control control;
	Graphics2D brush;
	Map map;

	public static void main(String[] args) {
		new Guest();
	}

	Guest() {
		control = new Control();
		painter = new Painter(control);
		brush = painter.getBrush();
		painter.doneDrawing();

		client = new Client();
		client.connect(ip);

		brush.setColor(Color.BLACK);
		brush.drawString("connected", 300, 300);
		painter.doneDrawing();

		map = new Map();

		long timeStart, timeEnd;
		while (true) {
			timeStart = System.currentTimeMillis();
			map.updateFromServer(client.read());
			map.paint(brush);
			painter.doneDrawing();
			if (control.esc == Control.JUST_PRESSED)
				painter.toggleDecorated();
			client.send(control.toMessage());
			control.outdate();
			Util.sleep(10);
			timeEnd = System.currentTimeMillis();
			painter.drawTime((int) (1000f / (timeEnd - timeStart)));
		}
	}
}
