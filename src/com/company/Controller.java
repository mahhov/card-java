package com.company;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Controller implements MouseInputListener, WindowListener {
	static final int CLICK_UP = 0, CLICK_DOWN = 1, CLICK_PRESSED = 2, CLICK_RELEASED = 3;
	int click;
	Cord mouseCord;
	boolean windowClosed;
	
	Controller() {
		mouseCord = new Cord();
	}
	
	int getClick() {
		switch (click) {
			case CLICK_PRESSED:
				click = CLICK_DOWN;
				return CLICK_PRESSED;
			case CLICK_RELEASED:
				click = CLICK_UP;
				return CLICK_RELEASED;
			default:
				return click;
		}
	}
	
	static boolean isDown(int click) {
		return click == CLICK_DOWN || click == CLICK_PRESSED;
	}
	
	class Cord {
		int x, y;
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
		click = CLICK_PRESSED;
	}
	
	public void mouseReleased(MouseEvent e) {
		click = CLICK_RELEASED;
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseDragged(MouseEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
		mouseCord.x = e.getX();
		mouseCord.y = e.getY() - Painter.borderSize;
	}
	
	public void windowOpened(WindowEvent e) {
	}
	
	public void windowClosing(WindowEvent e) {
		windowClosed = true;
	}
	
	public void windowClosed(WindowEvent e) {
	}
	
	public void windowIconified(WindowEvent e) {
	}
	
	public void windowDeiconified(WindowEvent e) {
	}
	
	public void windowActivated(WindowEvent e) {
	}
	
	public void windowDeactivated(WindowEvent e) {
	}
	
}
