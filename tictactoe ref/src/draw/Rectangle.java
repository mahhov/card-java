package draw;

import java.awt.Color;

public class Rectangle extends Draw {

	float x0, y0, width, height;
	Color color;

	public Rectangle(float x0, float y0, float width, float height, Color color) {
		this.x0 = x0;
		this.y0 = y0;
		this.width = width;
		this.height = height;
		this.color = color;
	}

	void paint(Painter painter) {
		painter.drawRectangle(x0, y0, width, height, color);
	}

}
