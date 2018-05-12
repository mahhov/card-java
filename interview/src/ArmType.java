import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ArmType implements KeyListener {

	JFrame frame;
	JTextArea box;
	JTextArea box2;
	Clipboard clpbrd;
	char[] chars;
	char p;
	int basep;
	String str;

	int LOWER_BASE = 0x0561;
	int UPPER_BASE = 0x0531;

	final String ENG_STRING[] = { "a", "b", "g", "d", "y", "z", "e", "u", "t",
			"zh", "i", "l", "x", "q", "kk", "h", "j", "xh", "tz", "m", "yy",
			"n", "sh", "o", "ch", "pp", "gh", "rr", "s", "v", "tt", "r", "c",
			"w", "p", "k", "oo", "f", "yv" };

	final char ENG_SINGLE[];

	final char ENG_MULTI[] = { 'z', 'k', 'x', 't', 'y', 's', 'c', 'p', 'g',
			'r', 'o', };

	final char PERIOD = 0x0589, QUESTION = 0x055E, QUOT_L = 0x00AB,
			QUOT_R = 0x00BB;

	ArmType() {
		ENG_SINGLE = new char[ENG_STRING.length];
		for (int i = 0; i < ENG_STRING.length; i++)
			if (ENG_STRING[i].length() == 1)
				ENG_SINGLE[i] = ENG_STRING[i].toCharArray()[0];
			else
				ENG_SINGLE[i] = '_';
		p = '_';

		box = new JTextArea(8, 30); // 8, 30
		box.setLineWrap(true);
		box.setWrapStyleWord(true);
		box.setFont(new Font("Sylfaen", Font.PLAIN, 30)); // Sylfaen
		System.out.println(box.getFont());

		box2 = new JTextArea(1, 15);
		box2.setFont(new Font("Sylfaen", Font.PLAIN, 30)); // Sylfaen
		box2.setBackground(Color.LIGHT_GRAY);

		JScrollPane upper = new JScrollPane(box);
		JPanel lower = new JPanel();
		lower.add(box2);

		frame = new JFrame();
		frame.add(upper, "North");
		frame.add(lower, "South");
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(this);
		box.addKeyListener(this);
		frame.setVisible(true);

		clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		chars = new char[100];
	}

	int[] findInArray(String[] array, char c) {
		int count = 0;
		for (int i = 0; i < array.length; i++)
			if (array[i].toCharArray()[0] == c)
				count++;

		int[] ret = new int[count];
		int j = 0;
		for (int i = 0; i < array.length; i++)
			if (array[i].toCharArray()[0] == c)
				ret[j++] = i;

		return ret;
	}

	int findInArray(char[] array, char c) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == c)
				return i;
		return -1;
	}

	int findInArray(String[] array, String s) {
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(s))
				return i;
		return -1;
	}

	char convertEngToArmSingle(char c, int base) {
		for (int i = 0; i < ENG_SINGLE.length; i++)
			if (ENG_SINGLE[i] == c)
				return (char) (base + i);

		return c;
	}

	char[] convertEngToArm(char c) {

		// return 0x0584;
		// System.out.println(Integer.toHexString(base + i));

		int base = LOWER_BASE;
		if (Character.isUpperCase(c)) {
			c = Character.toLowerCase(c);
			base = UPPER_BASE;
		}

		// p empty
		if (p == '_') {
			int i = findInArray(ENG_MULTI, c);
			if (i != -1) {
				p = c;
				basep = base;
				return null;
			}

			return new char[] { convertEngToArmSingle(c, base) };
		}

		// p set
		else {
			int i = findInArray(ENG_STRING, "" + p + c);
			if (i != -1) {
				p = '_';
				return new char[] { (char) (basep + i) };
			}

			char pa = convertEngToArmSingle(p, basep);
			p = '_';
			char[] ca = convertEngToArm(c);
			if (ca == null)
				return new char[] { pa };
			return new char[] { ca[0], pa };
		}

	}

	String getArmPrefixP() {
		String ret = "";
		int[] find = findInArray(ENG_STRING, p);

		for (int i : find) {
			ret += (char) (basep + i) + "(" + ENG_STRING[i] + ")" + " ";
		}

		return ret;
		// return "" + convertEngToArmSingle(p, LOWER_BASE);
	}

	char getArmPunctuation(char c) {
		switch (c) {
		case '?':
			return QUESTION;
		case ':':
			return PERIOD;
		case '\'':
			return QUOT_L;
		case '"':
			return QUOT_R;
		default:
			return '_';
		}
	}

	char emitP() {
		if (p == '_')
			return p;
		char r = convertEngToArmSingle(p, basep);
		p = '_';
		return r;
	}

	void copyToClipboard(String str) {
		clpbrd.setContents(new StringSelection(str), null);
	}

	public void keyPressed(KeyEvent arg0) {
		if (p != '_') {
			int i = box.getCaretPosition();
			box.setText(str);
			box.setCaretPosition(i - 1);
		}

		char c = arg0.getKeyChar();

		if (c == '`'
				|| (arg0.getKeyCode() == KeyEvent.VK_C && arg0.isControlDown())) {
			copyToClipboard(box.getText());
			arg0.consume();
		}

		if (Character.isLetter(c)) {
			int i = box.getCaretPosition();
			// box.insert("" + (char) (0x0582), i);
			char[] arm = convertEngToArm(c);
			if (arm != null)
				for (char carm : arm)
					box.insert("" + carm, i);

			arg0.consume();
		} else {
			char arm = emitP();
			int i = box.getCaretPosition();
			char punc = getArmPunctuation(c);
			if (punc != '_')
				box.insert("" + punc, i);
			if (arm != '_')
				box.insert("" + arm, i);
		}

		if (p == '_')
			box2.setText("");
		else {
			str = box.getText();
			int i = box.getCaretPosition();
			box.insert("" + convertEngToArmSingle(p, basep), i);
			box.setSelectionStart(i);
			box2.setText(getArmPrefixP());
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
		char c = arg0.getKeyChar();
		if (c == '`' || getArmPunctuation(c) != '_' || Character.isLetter(c))
			arg0.consume();
	}

	public static void main(String[] args) {
		new ArmType();
	}

}
