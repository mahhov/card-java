import java.awt.Color;

import javax.swing.UIDefaults;

public class HighlightColor {
	public static void main(String[] arg) {
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		Color a = defaults.getColor("List.selectionBackground");
		Color b = defaults.getColor("List.selectionForeground");
		
		System.out.println(a);
		System.out.println(b);
	}
}
