package player;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Control implements MouseMotionListener, KeyListener,
		MouseListener, MouseWheelListener {
	final int FRAME_SIZE;

	private Keys key;
	private Mouse mouse;

	// states
	static final int PRESS = 1, DOWN = 2, RELEASE = 3, UP = 0;

	// mouse
	public static final int LEFT = 1, MIDDLE = 2, RIGHT = 0;
	public static final int SCROLL_DOWN = 1, SCROLL_UP = -1;

	// keys (remember to update Keys.convertCharToKey)
	public static final int NUM_KEYS = 4;
	static final int KEY_W = 0, KEY_A = 1, KEY_S = 2, KEY_D = 3;

	private class Keys {

		int[] keyState;

		Keys() {
			keyState = new int[NUM_KEYS];
		}

		void press(int k) {
			if (k != -1 && keyState[k] != 2)
				keyState[k] = 1;
		}

		void release(int k) {
			if (k != -1)
				keyState[k] = 3;
		}

		private int get(int k) {
			int t = keyState[k];
			if (t == PRESS)
				keyState[k] = DOWN;
			else if (t == RELEASE)
				keyState[k] = UP;
			return t;
		}

		int convertAwtKeyCodeToKey(int keyCode) {
			switch (keyCode) {
			case KeyEvent.VK_W:
				return KEY_W;
			case KeyEvent.VK_A:
				return KEY_A;
			case KeyEvent.VK_S:
				return KEY_S;
			case KeyEvent.VK_D:
				return KEY_D;
			}

			return -1;
		}

	}

	private class Mouse {
		double x, y;
		int scroll;
		int[] mouseState;

		Mouse() {
			mouseState = new int[3];
		}

		void move(int x, int y) {
			this.x = 1.0 * x / FRAME_SIZE;
			this.y = 1.0 * y / FRAME_SIZE;
		}

		void scroll(int amount) {
			scroll += amount;
		}

		void setButton(int button, int value) {
			mouseState[button] = value;
		}

		private int get(int m) {
			int t = mouseState[m];
			if (t == PRESS)
				mouseState[m] = DOWN;
			else if (t == RELEASE)
				mouseState[m] = UP;
			return t;
		}

		private int getScroll() {
			int t = scroll;
			scroll = 0;
			return t;
		}

		int convertAwtButtonToButton(int button) {
			switch (button) {
			case MouseEvent.BUTTON1:
				return LEFT;
			case MouseEvent.BUTTON3:
				return RIGHT;
			case MouseEvent.BUTTON2:
				return MIDDLE;
			default:
				return -1;
			}
		}

		int convertLwjglButtonToButton(int button) {
			switch (button) {
			case 0:
				return Control.LEFT;
			case 1:
				return Control.RIGHT;
			case 2:
				return Control.MIDDLE;
			default:
				return -1; // for no mouse button (mouse move)
			}
		}

	}

	Control(int frameSize) {
		FRAME_SIZE = frameSize;

		key = new Keys();
		mouse = new Mouse();
	}

	int getKeyState(int k) {
		return key.get(k);
	}

	double getMouseX() {
		return mouse.x;
	}

	double getMouseY() {
		return mouse.y;
	}

	int getMouseState(int m) {
		return mouse.get(m);
	}

	int getMouseScroll() {
		return mouse.getScroll();
	}

	// PUBLIC KEY EVENT TRIGGERS

	public void keyPressAwt(int keyCode) {
		key.press(key.convertAwtKeyCodeToKey(keyCode));
	}

	public void keyReleaseAwt(int keyCode) {
		key.release(key.convertAwtKeyCodeToKey(keyCode));
	}

	// KEY EVENT LISTENERS

	public void keyPressed(KeyEvent e) {
		keyPressAwt(e.getKeyCode());
		// key.press(key.convertKeyCodeToKey(e.getKeyCode()));
	}

	public void keyReleased(KeyEvent e) {
		keyReleaseAwt(e.getKeyCode());
		// key.release(key.convertKeyCodeToKey(e.getKeyCode()));
	}

	// PUBLIC MOUSE EVENT TRIGGERS

	public void mouseMoveOrDrag(int mouseX, int mouseY) {
		mouse.move(mouseX, mouseY);
	}

	public void mousePressLwjgl(int button) {
		button = mouse.convertLwjglButtonToButton(button);
		if (button != -1)
			mouse.setButton(button, PRESS);
	}

	public void mouseReleaseLwjgl(int button) {
		button = mouse.convertLwjglButtonToButton(button);
		if (button != -1)
			mouse.setButton(button, RELEASE);
	}

	public void mousePressAwt(int button) {
		mouse.setButton(mouse.convertAwtButtonToButton(button), PRESS);
	}

	public void mouseReleaseAwt(int button) {
		mouse.setButton(mouse.convertAwtButtonToButton(button), RELEASE);
	}

	public void mouseScroll(int amount) {
		mouse.scroll(amount);
	}

	// MOUSE EVENT LISTENERS

	public void mouseDragged(MouseEvent e) {
		mouseMoveOrDrag(e.getX(), e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		mouseMoveOrDrag(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e) {
		mousePressAwt(e.getButton());
		// mouse.setButton(e.getButton(), PRESS);
	}

	public void mouseReleased(MouseEvent e) {
		mouseReleaseAwt(e.getButton());
		// mouse.setButton(e.getButton(), RELEASE);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		// positive getWheelRotation is scroll down
		mouseScroll(e.getWheelRotation());
	}

	// NOT USED

	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

}
