import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class CityScreen extends Screen {

	CityScreen(Graphics2D brush, int[] mouse) {
		super(brush, mouse);
	}

	public boolean drawCenterBox(String s, int row, int i, boolean selectable) {
		int left = 100 + 170 * i;
		int width = 150;
		int top = 200 + 60 * row;
		int height = 30;
		boolean select = (selectable && mouse[0] > left
				&& mouse[0] < left + width && mouse[1] > top && mouse[1] < top
				+ height);

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
