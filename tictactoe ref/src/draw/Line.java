package draw;

import java.awt.Color;

public class Line extends Draw {

	float x0, y0, x1, y1;
	int width;
	Color color;

	public Line(float x0, float y0, float x1, float y1, int width) {
		this(x0, y0, x1, y1, width, Color.BLACK);
	}

	public Line(float x0, float y0, float x1, float y1, int width, Color color) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.width = width;
		this.color = color;
	}

	void paint(Painter painter) {
		painter.drawLine(x0, y0, x1, y1, width, color);
	}

}
