import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Painter extends JFrame {

	BufferedImage canvas;
	Graphics2D brush;

	public Painter(Control control) {
		super();

		final int width = 900, height = 750;

		this.setSize(width, height);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((dim.width - width) / 2, (dim.height - height) / 2);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addMouseListener(control);
		this.addMouseMotionListener(control);
		this.addKeyListener(control);
		this.setVisible(true);

		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		brush = canvas.createGraphics();
		brush.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
	}

	public Graphics2D getBrush() {
		return brush;
	}

	public void paint(Graphics g) {
		if (brush != null) {
			// draw
			g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);

			// erase
			// if (fade)
			// 0x29001F
			brush.setColor(new Color(0x002540));
			// else
			// brush.setColor(Color.WHITE);
			brush.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}
