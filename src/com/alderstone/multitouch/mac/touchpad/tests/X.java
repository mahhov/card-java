package com.alderstone.multitouch.mac.touchpad.tests;

import javax.swing.*;
import java.awt.event.*;

public class X extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	
	X() {
		super();
		setSize(800, 800);
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
	}
	
	public static void main(String[] arg) {
		new X();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("click " + e.getButton());
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("press " + e.getButton());
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("release " + e.getButton());
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("wheel " + e.getPreciseWheelRotation() + " " + e.getScrollAmount() + " " + e.getScrollType() + " " + e.getUnitsToScroll() + " " + e.getWheelRotation() + " " + e.getModifiers() + " " + e.getModifiersEx());
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
}
