package world;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import message.Controls;

public class Control implements MouseListener, MouseMotionListener, KeyListener {

	public static final int RELEASED = 0, JUST_PRESSED = 1, PRESSED = 2,
			JUST_RELEASED = 3;
	// 0 = release, 1 = just pressed, 2 = down, 3 = just released
	public int esc;
	public int up, left, right, down;
	public int mouseL, mouseR;
	public int mousex, mousey;

	public void mouseDragged(MouseEvent arg0) {
		mousex = arg0.getX();
		mousey = arg0.getY();
	}

	public void mouseMoved(MouseEvent arg0) {
		mousex = arg0.getX();
		mousey = arg0.getY();
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1)
			mouseL = JUST_PRESSED;
		else if (arg0.getButton() == MouseEvent.BUTTON3)
			mouseR = JUST_PRESSED;
	}

	public void mouseReleased(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1)
			mouseL = JUST_RELEASED;
		else if (arg0.getButton() == MouseEvent.BUTTON3)
			mouseR = JUST_RELEASED;
	}

	public void keyPressed(KeyEvent e) {
		set(e.getKeyCode(), JUST_PRESSED);
	}

	public void keyReleased(KeyEvent e) {
		set(e.getKeyCode(), JUST_RELEASED);
	}

	void set(int keyCode, int value) {
		switch (keyCode) {
		case KeyEvent.VK_ESCAPE:
			esc = value;
			break;
		case KeyEvent.VK_A:
			left = value;
			break;
		case KeyEvent.VK_D:
			right = value;
			break;
		case KeyEvent.VK_S:
			down = value;
			break;
		case KeyEvent.VK_W:
			up = value;
			break;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public static boolean isPressed(int state) {
		return state == JUST_PRESSED || state == PRESSED;
	}

	Controls toMessage() {
		return new Controls((byte) up, (byte) left, (byte) right, (byte) down, mousex, mousey);
	}

	public void outdate() {
		if (up == JUST_PRESSED)
			up = PRESSED;
		if (down == JUST_PRESSED)
			down = PRESSED;
		if (left == JUST_PRESSED)
			left = PRESSED;
		if (right == JUST_PRESSED)
			right = PRESSED;
		if (esc == JUST_PRESSED)
			esc = PRESSED;
		
		if (up == JUST_RELEASED)
			up = RELEASED;
		if (down == JUST_RELEASED)
			down = RELEASED;
		if (left == JUST_RELEASED)
			left = RELEASED;
		if (right == JUST_RELEASED)
			right = RELEASED;
		if (esc == JUST_RELEASED)
			esc = RELEASED;
	}

}
