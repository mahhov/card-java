package draw;

import java.awt.Color;

import javax.swing.JFrame;

import list.Queue;

public abstract class Painter extends JFrame {
	final int FRAME_SIZE;
	int count;
	boolean pause;

	Queue<Draw> draw;
	Queue<Draw> overlay;
	String[] write;

	Painter(int frameSize) {
		FRAME_SIZE = frameSize;

		draw = new Queue<Draw>();

		overlay = new Queue<Draw>();

		write = new String[5];
		for (int i = 0; i < write.length; i++)
			write[i] = "";
	}

	public void pause() {
		pause = !pause;
		dispose();
		setUndecorated(!pause);
		setVisible(true);
	}
	
	public abstract void paint();

	public void write(String s, int line) {
		if (line < write.length && line >= 0)
			write[line] = s;
		else
			System.out.println("line " + line + " out of write buffer : " + s);
	}

	public void addDraw(Draw d) {
		draw.add(d);
	}

	public void addOverlay(Draw d) {
		overlay.add(d);
	}

	abstract void drawLine(float x0, float y0, float x1, float y1,
			int thickness, Color color);

	abstract void drawRectangle(float x0, float y0, float width, float height,
			Color color);

}
