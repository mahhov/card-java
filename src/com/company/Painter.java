package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class Painter extends JFrame {
	
	final static Color BACK_COLOR = new Color(220, 220, 220);
	final static int WIDTH = 800, HEIGHT = Card.HEIGHT * 8 + Card.DRAW_MARGIN_Y * 7 + Card.DRAW_MARGIN * 2;
	static int borderSize = 0;
	Graphics2D brush;
	BufferedImage canvas;
	
	
	Painter(Controller controller) {
		super();
		
		canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		brush = (Graphics2D) canvas.getGraphics();
		
		this.getContentPane().setSize(WIDTH, HEIGHT);
		this.pack();
		borderSize = this.getHeight();
		setSize(WIDTH, HEIGHT + borderSize);
		this.setLocationRelativeTo(null);
		addMouseListener(controller);
		addMouseMotionListener(controller);
		addWindowListener(controller);
		setVisible(true);
	}
	
	public void clearDraw(boolean top) {
		brush.setBackground(BACK_COLOR);
		brush.clearRect(0, 0, WIDTH, HEIGHT);
		
		brush.setColor(Color.LIGHT_GRAY);
		if (top)
			brush.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);
		else
			brush.fillRect(0, 0, WIDTH, HEIGHT / 2);
		
		brush.setStroke(new BasicStroke(5));
		brush.setColor(Color.darkGray);
		brush.drawRect(0, 0, WIDTH, HEIGHT);
	}
	
	public void paint(Graphics graphics) {
		graphics.drawImage(canvas, 0, borderSize, null);
	}
}
