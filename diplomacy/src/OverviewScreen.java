import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class OverviewScreen extends Screen {

	OverviewScreen(Graphics2D brush, int[] mouse) {
		super(brush, mouse);
	}

	public void drawCenterText(String s, int section, int column, int line,
			int miniColumn) {
		if (brush != null) {
			brush.setFont(FONT_REGULAR);
			brush.setColor(Color.WHITE);
			brush.drawString(s, 60 + column * 375 + 165 * miniColumn, line * 15
					+ 85 + 110 * section);
		}
	}

	public void drawRightText(String s, int section) {
		if (brush != null) {
			brush.setFont(FONT_REGULAR);
			brush.setColor(Color.WHITE);
			brush.drawString(s, 790, 95 + 45 * section);
		}
	}

	public boolean drawCenterBox(int section, int column) {
		int left = 50 + column * 375;
		int width = 325;
		int top = 80 + 110 * section - 15;
		int height = 90;
		boolean select = (mouse[0] > left && mouse[0] < left + width
				&& mouse[1] > top && mouse[1] < top + height);

		if (brush != null) {
			if (select) {
				brush.setColor(HIGHLIGHT_COLOR);
				brush.fillRect(left, top, width, height);
			}
			brush.setColor(Color.WHITE);
			brush.drawRect(left, top, width, height);
		}
		return select && mouse[2] == 1;
	}

	public boolean drawRightBox(int section) {
		int left = 780;
		int width = 95;
		int top = 90 + 45 * section - 15;
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
		}
		return select && mouse[2] == 1;
	}

}
