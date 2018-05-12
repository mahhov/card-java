package raytrace;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Engine2 extends JFrame implements MouseMotionListener,
		MouseListener, MouseWheelListener {

	Camera c;
	Scene s;
	boolean ready;

	float theta, phi, distance = 50;
	int mx, my;
	boolean m;

	public static void main(String[] args) {
		new Engine2();
	}

	private Engine2() {
		s = new Scene(4);

		Sphere sphere1 = new Sphere(new Vector(0, 0, 0), 10, new Vector(.1f,
				.6f, .0f), new Vector(1f, 1f, 0f), new Vector(.8f, .8f, .8f),
				16, 0.5f); // yellow
		Sphere sphere2 = new Sphere(new Vector(25, 50, 0), 10, new Vector(.1f,
				.1f, .1f), new Vector(0f, 0f, .7f), new Vector(0, 0, 1), 25,
				0.5f); // blue
		Sphere sphere3 = new Sphere(new Vector(-15, 0, 0), 5, new Vector(.3f,
				.1f, .0f), new Vector(1f, 0f, 0f), new Vector(.8f, .8f, .8f),
				160, .5f); // red
		Sphere sphere4 = new Sphere(new Vector(-15, 0, 10), 5, new Vector(.3f,
				.1f, .0f), new Vector(1f, 0f, 0f), new Vector(.8f, .8f, .8f),
				160, .5f); // red
		Cube cube = new Cube(new Vector(-15, -15, -15), new Vector(30, 0, 0),
				new Vector(0, 30, 0), new Vector(0, 0, 10), new Vector(.0f,
						.1f, .0f), new Vector(0f, .5f, .5f), new Vector(1f, 0,
						0), 16, .5f);
		s.add(sphere2);
		s.add(sphere1);
		s.add(sphere3);
		s.add(sphere4);
		s.add(cube);
		s.add(new Light(new Vector(-50, 30, -30), new Vector(.6f, .6f, .6f)),
				true);
		c = new Camera(0, -50, 0, 0, 1, 0, 1.23f, 1.23f);

		this.setSize(500, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);

		float x, y, z;
		long t = System.currentTimeMillis(), t1 = t;

		while (true) {
			while (!ready) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// t1 = System.currentTimeMillis();
			// theta += .001f * (t1 - t);
			// t = t1;
			float r = (float) (-distance * Math.cos(phi));
			x = (float) (r * Math.sin(theta));
			y = (float) (r * Math.cos(theta));
			z = (float) (-50 * Math.sin(phi));
			c = new Camera(x, y, z, -x, -y, -z, 1.23f, 1.23f);
			this.repaint();
		}
	}

	public void paint(Graphics g) {
		final int res = 500;
		ready = false;
		BufferedImage i = s.drawThread(res, res, c);
		this.getGraphics().drawImage(i, 0, 0, 500, 500, 0, 0, res, res, null);
		ready = true;
	}

	public void mouseDragged(MouseEvent e) {
		if (!m)
			return;

		int x = e.getX();
		theta += (x - mx) / 100f;
		mx = x;

		int y = e.getY();
		phi -= (y - my) / 100f;
		my = y;
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		m = true;
		mx = arg0.getX();
		my = arg0.getY();
	}

	public void mouseReleased(MouseEvent arg0) {
		m = false;
	}

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		if (arg0.getWheelRotation() < 0)
			distance -= 5;
		else
			distance += 5;
	}
}