package com.alderstone.multitouch.mac.touchpad.tests;

import com.alderstone.multitouch.mac.touchpad.Finger;
import com.alderstone.multitouch.mac.touchpad.FingerState;
import com.alderstone.multitouch.mac.touchpad.TouchpadObservable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.Observable;
import java.util.Observer;

class Fingers implements Observer {
	private static final int MAX_FINGER_BLOBS = 20;
	
	private int width, height;
	TouchpadObservable tpo;
	Finger blobs[] = new Finger[MAX_FINGER_BLOBS];
	
	public Fingers(int width, int height) {
		this.width = width;
		this.height = height;
		tpo = TouchpadObservable.getInstance();
		tpo.addObserver(this);
	}
	
	// Multitouch updateGlide event
	public void update(Observable obj, Object arg) {
		// The event 'arg' is of type: com.alderstone.multitouch.mac.touchpad.Finger
		Finger f = (Finger) arg;
		int id = f.getID();
		if (id <= MAX_FINGER_BLOBS)
			blobs[id - 1] = f;
	}
	
	public void update() {
	}
	
	public void draw(Graphics g) {
		
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (int i = 0; i < MAX_FINGER_BLOBS; i++) {
			Finger f = blobs[i];
			if (f != null && f.getState() == FingerState.PRESSED) {
				
				int x = (int) (width * (f.getX()));
				int y = (int) (height * (1 - f.getY()));
				int xsize = (int) (10 * f.getSize() * (f.getMajorAxis() / 2));
				int ysize = (int) (10 * f.getSize() * (f.getMinorAxis() / 2));
				int ang = f.getAngle();
				
				Ellipse2D ellipse = new Ellipse2D.Float(0, 0, xsize, ysize);
				
				AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
				at.translate(x - xsize / 2, y - ysize / 2);
				at.rotate((Math.PI / 180) * -ang, xsize / 2, ysize / 2);  // convert degrees to radians
				
				g.setColor(Color.PINK);
				((Graphics2D) g).fill(at.createTransformedShape(ellipse));
				
				g.setColor(Color.DARK_GRAY);
				g.drawString("" + i, x, y);
			}
		}
	}
}


public class SwingTest extends JFrame {
	
	private static final int SURFACE_WIDTH = 800;
	private static final int SURFACE_HEIGHT = 600;
	private static final int UPDATE_RATE = 30;  // number of updateGlide per second
	private static final long UPDATE_PERIOD = 1000L / UPDATE_RATE;  // milliseconds
	
	private Fingers fingers;
	private SurfaceCanvas surface;
	
	public SwingTest() {
		surface = new SurfaceCanvas();
		surface.setPreferredSize(new Dimension(SURFACE_WIDTH, SURFACE_HEIGHT));
		this.setContentPane(surface);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setTitle("Mac OS X Multitouch Event API Swing Test - wayne.keenan@gmail.com");
		this.setVisible(true);
		
		surfaceStart();
	}
	
	public void surfaceStart() {
		fingers = new Fingers(SURFACE_WIDTH, SURFACE_HEIGHT);
		
		Thread surfaceThread = new Thread() {
			public void run() {
				while (true) {
					surfaceUpdate();
					repaint();
					try {
						Thread.sleep(UPDATE_PERIOD);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		surfaceThread.start();
	}
	
	public void surfaceUpdate() {
		fingers.update();
	}
	
	class SurfaceCanvas extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(Color.WHITE);
			fingers.draw(g);
		}
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SwingTest();
			}
		});
	}
}

