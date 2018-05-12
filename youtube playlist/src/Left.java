import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Left {

	int curTrack = 0;
	File[] playlist;

	public static void main(String[] arg) throws IOException {
		new Left();
	}

	public Left() {
		constructFrame();

		String folderPath = "C:\\Users\\m\\Desktop\\media\\music\\youtube videos\\";
		File folder = new File(folderPath);
		playlist = folder.listFiles();
		shuffle(playlist);

		playNext();
	}

	public void playNext() {
		try {
			Desktop.getDesktop().open(playlist[curTrack]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shuffle(File[] list) {
		for (int i = 0; i < list.length; i++) {
			int nextSong = i + (int) (Math.random() * (list.length - i));
			File t = list[i];
			list[i] = list[nextSong];
			list[nextSong] = t;
		}
	}

	public JFrame constructFrame() {
		JFrame frame = new JFrame();
		// frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setSize(100, 100);
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton nextButton = new JButton("next");
		nextButton.setActionCommand("next");

		JButton prevButton = new JButton("prev");
		prevButton.setActionCommand("prev");

		final JLabel trackLabel = new JLabel("" + curTrack);

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getActionCommand() == "next") {
					if (++curTrack >= playlist.length)
						curTrack -= playlist.length;
					trackLabel.setText("" + curTrack);
					playNext();
				} else if (arg0.getActionCommand() == "prev") {
					if (--curTrack < 0)
						curTrack += playlist.length;
					trackLabel.setText("" + curTrack);
					playNext();
				}
			}
		};

		nextButton.addActionListener(actionListener);
		prevButton.addActionListener(actionListener);

		JPanel panel = new JPanel();
		panel.add(nextButton);
		panel.add(prevButton);
		panel.add(trackLabel);
		frame.add(panel);

		frame.pack();
		frame.setVisible(true);
		return frame;
	}
}
