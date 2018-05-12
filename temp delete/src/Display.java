import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * deals with user input and output
 */
public class Display {
	private JFrame window;
	private Panel panel;
	private Image canvas;
	private Graphics brush;
	public Input input;

	/**
	 * constructs Display with defaults
	 */
	Display() {
		input = new Input();
		window = new JFrame();
		window.setResizable(false);
		panel = new Panel();
		window.add(panel);
		window.pack();
		window.setVisible(true);
		canvas = new BufferedImage(panel.getSize().width,
				panel.getSize().height, BufferedImage.TYPE_INT_RGB);
		brush = canvas.getGraphics();
	}

	class Input {
		private int x, y;
		private int mouse;// 0=nothing, 1=just down, 2=just up
		private int scroll;

		public Input() {
			mouse = 0;
			scroll = 0;
			x = 0;
			y = 0;
		}

		public int[] getMouse() {
			int[] result = { mouse, x, y, scroll };
			mouse = 0;
			scroll = 0;
			return result;
		}

		private void down(int x, int y) {
			this.x = x;
			this.y = y;
			mouse = 1;
		}

		private void up(int x, int y) {
			this.x = x;
			this.y = y;
			mouse = 2;
		}

		public void move(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void scroll(int scroll) {
			this.scroll += scroll;
		}
	}

	public Graphics getGraphics() {
		return brush;
	}

	/**
	 * updates display
	 * 
	 * @return true if game is still continueing, false otherwise
	 */
	public boolean run() {
		panel.repaint();
		return (window.isVisible());
	}

	/**
	 * extends JPanel, overloads getPrefferedSize and paintComponent *
	 */
	private class Panel extends JPanel implements MouseListener,
			MouseMotionListener, MouseWheelListener {

		public Panel() {
			super();
			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(this);
		}

		public Dimension getPreferredSize() {
			return new Dimension(800, 800);
		}

		public void paintComponent(Graphics g) {
			g.drawImage(canvas, 0, 0, this);
			brush.clearRect(0, 0, 800, 800);
		}

		public void mouseClicked(MouseEvent arg0) {
		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent arg0) {
			input.down(arg0.getX(), arg0.getY());
		}

		public void mouseReleased(MouseEvent arg0) {
			input.up(arg0.getX(), arg0.getY());
		}

		public void mouseDragged(MouseEvent e) {
			input.move(e.getX(), e.getY());
		}

		public void mouseMoved(MouseEvent e) {
			input.move(e.getX(), e.getY());
		}

		public void mouseWheelMoved(MouseWheelEvent arg0) {
			input.scroll(arg0.getWheelRotation());
		}
	}
}
