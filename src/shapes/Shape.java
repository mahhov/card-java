package shapes;

import engine.Camera;

import java.awt.*;

public class Shape {
	static final int TOP = 0, BOTTOM = 1, FRONT = 2, BACK = 3, RIGHT = 4, LEFT = 5;
	
	double x, y, z;
	double rot, tilt;
	
	public Shape(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Shape(double x, double y, double z, double rot, double tilt) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rot = rot;
		this.tilt = tilt;
	}
	
	public void draw(Graphics2D brush, Camera c) {
	}
	
	boolean obstruct(int side) {
		return false;
	}
	
}
