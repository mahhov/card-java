package draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import player.Control;

public class PainterJava extends Painter {
	BufferedImage canvas;
	Graphics2D brush;

	public PainterJava(int frameSize, Control control) {
		super(frameSize);

		setResizable(false);
		// setSize(FRAME_SIZE, FRAME_SIZE);
		setUndecorated(true);
		setPreferredSize(new Dimension(FRAME_SIZE, FRAME_SIZE));
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		addKeyListener(control);
		addMouseMotionListener(control);
		addMouseListener(control);
		addMouseWheelListener(control);

		canvas = new BufferedImage(FRAME_SIZE, FRAME_SIZE,
				BufferedImage.TYPE_INT_RGB);

		brush = (Graphics2D) canvas.getGraphics();
		brush.setFont(new Font("monospaced", Font.PLAIN, 25));
	}

	public void paint() {
		Draw d;
		while ((d = draw.remove()) != null)
			d.paint(this);

		// while ((d = overlay.remove()) != null)
		// rectangle(d.xywidthheight, d.thick, d.color, d.alpha);

		if (brush != null) {
			brush.setColor(Color.BLACK);
			for (int i = 0; i < write.length; i++) {
				brush.drawString(write[i], 10, 30 + 30 * i);
				write[i] = "";
			}
		}

		paint(getGraphics());
	}

	public void paint(Graphics g) {
		if (g != null) {
			// write("paint count: " + count, 4);
			count = 0;

			// draw
			g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);

			// erase
			brush.setColor(Color.white);
			brush.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	private void setColor(Color color) {
		brush.setColor(color);
	}

	@SuppressWarnings("unused")
	private void setColor(Color color, float alpha) {
		// transparency results in 3 frames drop per second
		if (alpha >= 1)
			setColor(color);
		else
			brush.setColor(new Color(color.getRed(), color.getGreen(), color
					.getBlue(), (int) (alpha * 255)));
	}

	private void setLineWidth(int width) {
		brush.setStroke(new BasicStroke(width));
	}

	void drawLine(float x0, float y0, float x1, float y1, int width, Color color) {
		setColor(color);
		setLineWidth(width);
		brush.drawLine((int) (x0 * FRAME_SIZE), (int) (y0 * FRAME_SIZE),
				(int) (x1 * FRAME_SIZE), (int) (y1 * FRAME_SIZE));
	}

	void drawRectangle(float x0, float y0, float width, float height,
			Color color) {
		setColor(color);
		setLineWidth(1);
		brush.fillRect((int) (x0 * FRAME_SIZE), (int) (y0 * FRAME_SIZE),
				(int) (width * FRAME_SIZE), (int) (height * FRAME_SIZE));
	}
}