package world;

import engine.Camera;
import shapes.Cube;
import shapes.Shape;

import java.awt.*;


public class World {
	int width, height, depth;
	Shape[][][] cell;
	
	public World(int width, int height, int depth) {
		cell = new Shape[width][height][depth];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				if (Math.random() > 0.8)
					cell[x][y][1] = new Cube(x + 0.5, y + 0.5, 1.5);
				cell[x][y][0] = new Cube(x + 0.5, y + 0.5, 0.5);
			}
	}
	
	public void draw(Graphics2D brush, Camera c) {
		// get cull boundaries
		
		for (int x = 0; x < c.x; x++)
			drawRow(brush, c, x);
		for (int x = width - 1; x > c.x; x--)
			drawRow(brush, c, x);
		drawRow(brush, c, (int) c.x);
	}
	
	private void drawRow(Graphics2D brush, Camera c, int x) {
		for (int y = 0; y < c.y; y++)
			drawColumn(brush, c, x, y);
		for (int y = width - 1; y > c.y; y--)
			drawColumn(brush, c, x, y);
		drawColumn(brush, c, x, (int) c.y);
	}
	
	private void drawColumn(Graphics2D brush, Camera c, int x, int y) {
		for (int z = 0; z < c.z; z++)
			drawCell(brush, c, x, y, z);
		for (int z = width - 1; z > c.z; z--)
			drawCell(brush, c, x, y, z);
		drawCell(brush, c, x, y, (int) c.z);
	}
	
	private void drawCell(Graphics2D brush, Camera c, int x, int y, int z) {
		cell[x][y][z].draw(brush, c);
	}
}
