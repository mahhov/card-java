package clock;

import java.awt.Font;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Clock {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Hello!!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		// frame.setUndecorated(true);
		JLabel time = new JLabel("--time--");
		time.setFont(new Font("Arial", Font.BOLD, 36));
		frame.setLocationByPlatform(true);
		frame.add(time);
		frame.pack();
		frame.setVisible(true);
		Date d;
		String timeString;
		while (true) {
			d = new Date();
			timeString = d.getHours() + ":" + d.getMinutes();
			time.setText(timeString);
			try {
				Thread.sleep(10000);
				// Thread.sleep(60000 - d.getSeconds());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}