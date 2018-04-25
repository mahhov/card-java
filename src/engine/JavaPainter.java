package engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JavaPainter extends JFrame {
	final int WIDTH;
	final int HEIGHT;
	static int borderSize = 0;
	Graphics2D brush;
	BufferedImage canvas;
	
	JavaPainter(int frameSize) {
		WIDTH = frameSize;
		HEIGHT = frameSize;
		canvas = new BufferedImage(WIDTH, HEIGHT, 1);
		brush = (Graphics2D) canvas.getGraphics();
		getContentPane().setSize(WIDTH, HEIGHT);
		pack();
		borderSize = this.getHeight();
		setSize(WIDTH, HEIGHT + borderSize);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	void clearDraw() {
		this.brush.setBackground(Color.WHITE);
		this.brush.clearRect(0, 0, WIDTH, HEIGHT);
	}
	
	public void paint(Graphics graphics) {
		graphics.drawImage(this.canvas, 0, borderSize, null);
	}
}
	