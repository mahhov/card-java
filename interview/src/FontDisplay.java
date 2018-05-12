import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FontDisplay extends JPanel {

	public static void main(String[] a) {
		JFrame f = new JFrame();
		f.setSize(400, 400);
		f.add(new FontDisplay());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	public void paint(Graphics g) {
		String str = "";
		char a = 0x0561;
		char h = 0x0570;
		char k = 0x0584;
		char ff = 0x0586;
		char v = 0x057E;
		char m = 0x0574;
		char j = 0x0573;
		char j2 = 0x056E;
		char j3 = 0x057B;
		char j4 = 0x0571;
		char ch = 0x0579;
		char t = 0x0569;
		str = "" + a + h + k + ff + v + m + j + j2 + j3 + j4 + ch + t;

		int maxLines = 30;

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		Font[] allFonts = ge.getAllFonts();

		j = 0;
		for (int i = 0; i < allFonts.length; i++) {
			if (i == 27 || (i >= 147 && i <= 154) || (i >= 321 && i <= 324)
					|| (i >= 356 && i <= 359) || (i >= 372 && i <= 375)
					|| i == 393 || i == 399 || i == 400) {
				// if (i == 147) {
				Font f = allFonts[i].deriveFont(20f);
				g.setFont(f);
				g.setColor(Color.black);
				g.drawString(str + " " + i, 100 + 300 * (j / maxLines),
						100 + 30 * (j % maxLines));
				j++;
				g.drawString(f.getFontName(), 100 + 300 * (j / maxLines),
						100 + 30 * (j % maxLines));
				j++;
			}
		}
	}
}