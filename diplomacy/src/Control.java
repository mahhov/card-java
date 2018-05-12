import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Control implements MouseListener, MouseMotionListener, KeyListener {

	int x, y, button;
	int num; // -1 = no press, -2 = backspace/delete

	// int[] data; // x, y, button (0 none, 1 left, 2 right)

	Control() {
		num = -1;
	}

	public void mouseDragged(MouseEvent arg0) {
	}

	public void mouseMoved(MouseEvent arg0) {
		x = arg0.getX();
		y = arg0.getY();
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1)
			button = 1;
	}

	public void mouseReleased(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1)
			button = 0;
	}

	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if (k == 127 || k == 8)
			num = -2;
		else {
			k -= 48;
			if (k >= 0 && k < 10)
				num = k;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public int[] getData() {
		int[] r = new int[] { x, y, button };
		button = 0;
		return r;
	}

	public int getNum() {
		int r = num;
		num = -1;
		return r;
	}

}
