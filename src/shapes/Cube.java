package shapes;

import engine.Camera;

import java.awt.*;

public class Cube extends Shape {
	
	public Cube(double x, double y, double z) {
		super(x, y, z);
	}
	
	public void draw(Graphics2D brush, Camera c) {
	}
	
	boolean obstruct(int side) {
		return false;
	}
	
}
