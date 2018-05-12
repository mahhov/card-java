import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Screen {

	public final static Color HIGHLIGHT_COLOR = new Color(0x005060);
	public final static Font FONT_REGULAR = new Font(Font.MONOSPACED,
			Font.PLAIN, 15);
	public final static Font FONT_TITLE = new Font(Font.MONOSPACED, Font.PLAIN,
			30);

	Graphics2D brush;
	int[] mouse;

	Screen(Graphics2D brush, int[] mouse) {
		this.brush = brush;
		this.mouse = mouse;
	}

	public void drawBoxedTopText(String s) {
		if (brush != null) {
			brush.setColor(Color.WHITE);
			brush.drawRect(50, 50, 800, 50);
			brush.setFont(FONT_TITLE);
			int strWidth = brush.getFontMetrics().stringWidth(s);
			brush.drawString(s, 450 - strWidth / 2, 83);
		}
	}

	boolean drawBottomBox(String str) {
		int left = 770;
		int width = 100;
		int top = 690;
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
			brush.drawString(str, 785, 710);
		}

		return select && mouse[2] == 1;
	}
}
