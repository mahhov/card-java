import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class NationScreen extends Screen {

	NationScreen(Graphics2D brush, int[] mouse) {
		super(brush, mouse);
	}

	public void drawBoxes() {
		if (brush != null) {
			brush.setColor(Color.WHITE);
			brush.drawRect(30, 120, 200, 135); // left top
			brush.drawRect(670, 120, 200, 135); // right
			brush.drawRect(30, 275, 200, 435); // left bottom
			brush.drawRect(250, 120, 400, 590); // center
		}
	}

	public void drawSideText(String s, int row, int col) {
		int left = 45 + 640 * col;
		int top = 140 + 20 * row;

		if (brush != null) {
			brush.setFont(FONT_REGULAR);
			brush.drawString(s, left, top);
		}
	}

	public boolean drawLeftText(String s, int row) {
		int left = 40;
		int width = 180;
		int top = 290 + 40 * row;
		int height = 30;
		boolean select = (mouse[0] > left && mouse[0] < left + width
				&& mouse[1] > top && mouse[1] < top + height);

		if (brush != null) {
			if (select) {
				brush.setColor(HIGHLIGHT_COLOR);
				brush.fillRect(left, top, width, height);
			}
			brush.setColor(Color.WHITE);
			brush.drawRect(left, top, width, height);

			brush.setFont(FONT_REGULAR);
			brush.drawString(s, left + 10, top + 20);
		}
		return select && mouse[2] == 1;
	}

	public boolean drawCenterBox(String s, int row) {
		int left = 300;
		int width = 300;
		int top = 150 + 40 * row;
		int height = 30;
		boolean select = (mouse[0] > left && mouse[0] < left + width
				&& mouse[1] > top && mouse[1] < top + height);

		if (brush != null) {
			if (select) {
				brush.setColor(HIGHLIGHT_COLOR);
				brush.fillRect(left, top, width, height);
			}
			brush.setColor(Color.WHITE);
			brush.drawRect(left, top, width, height);

			brush.setFont(FONT_REGULAR);
			brush.drawString(s, left + 10, top + 20);
		}
		return select && mouse[2] == 1;
	}

}
