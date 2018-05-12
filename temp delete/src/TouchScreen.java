import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.media.*;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.*;
import javax.swing.border.Border;

public class TouchScreen {
	
	private JFrame						frame;
	private boolean					running;
	private Player						_player;
	private JPanel						panel1;				// 1=webcam
	private JPanelM					panel2;				// 2=modified
	private Image						image;
	private BufferedImage			imageB;
	private FrameGrabbingControl	frameGrabber;
	private Robot						robot;
	private Dimension					screenSize;
	private boolean					real;
	private int[]						constants;			// clickConstant,maxDarkness,minDarkness,initialized
	private JTextArea					outputText;
	private boolean					mouseDown;
	private boolean					lightMode;
	private int							lastDarkValue;
	private int[]						lastDarkLocation;
	private boolean[]					modeSS;				// speed press sensitive, average move sensitive
																	
	private class JPanelM extends JPanel {
		
		int	x, y, w, h; // these are for the circle of mouse
		int	color;		// read above
								
		JPanelM() {
			super();
			x = 0;
			y = 0;
			w = 10;
			h = 10;
			color = -1;
		}
		
		private void redraw(int[] t_) {
			if (t_ != null) {
				if (t_[0] > 0) {
					if (color == -1 | modeSS[1] == false) { // modeSS[1]=true when want to average new and old locations
						x = (t_[0] - w / 2);
						y = (t_[1] - h / 2);
					} else {
						x = (t_[0] - w / 2 + x) / 2;
						y = (t_[1] - h / 2 + y) / 2;
					}
				}
				color = t_[2];
			} else
				color = -1;
			this.repaint();
		}
		
		public void paintComponent(Graphics g) {
			g.drawImage(imageB, 0, 0, null);
			if (color != -1) {
				if (color == 0) {
					g.setColor(Color.red);
				} else {
					g.setColor(Color.green);
				}
				g.drawOval(x, y, w, h);
			}
		}
	}
	
	TouchScreen() {
		lastDarkValue = -1;
		lastDarkLocation = new int[2];
		real = false;
		lightMode = false;
		mouseDown = false;
		imageB = null;
		image = null;
		frame = new JFrame();
		frame.addKeyListener(new KeyListener() {
			
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == arg0.VK_ESCAPE) {
					running = false;
				}
			}
			
			public void keyReleased(KeyEvent arg0) {
			}
			
			public void keyTyped(KeyEvent arg0) {
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent winEvt) {
				running = false;
			}
		});
		JPanel panel = (JPanel) frame.getContentPane();
		panel1 = new JPanel();
		panel2 = new JPanelM();
		panel.setLayout(new BorderLayout());
		try {
			_player = Manager.createRealizedPlayer(new MediaLocator("vfw:Microsoft WDM Image Capture (Win32):0"));
			if (_player.getVisualComponent() != null) {
				panel1.add(_player.getVisualComponent());
			}
		} catch (Exception e) {
			System.out.println("error 1");
		}
		frameGrabber = (FrameGrabbingControl) _player.getControl("javax.media.control.FrameGrabbingControl");
		_player.start();
		try {
			robot = new Robot();
		} catch (AWTException e) {
			System.out.println("error 2");
		}
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Border border = BorderFactory.createLineBorder(Color.black);
		panel1.setPreferredSize(new Dimension(400, 400));
		panel1.setBorder(border);
		panel2.setPreferredSize(new Dimension(400, 400));
		panel2.setBorder(border);
		outputText = new JTextArea();
		outputText.setPreferredSize(new Dimension(500, 200));
		outputText.setBorder(border);
		outputText.setEditable(false);
		outputText.setAutoscrolls(true);
		JPanel panel3 = new JPanel(); // for buttons
		panel3.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JButton butStart = new JButton("Start");
		butStart.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				real = true; // start
				outText("start", true);
			}
		});
		butStart.setPreferredSize(new Dimension(100, 30));
		JButton butStop = new JButton("Stop");
		butStop.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				real = false; // stop
				outText("stop", true);
			}
		});
		butStop.setPreferredSize(new Dimension(100, 30));
		JButton butRestartD = new JButton("Setup Shadow");
		butRestartD.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				real = false; // restart
				lightMode = false;
				constants = new int[] { 1, 99000000, 0, 0 };
				outputText.setText("");
				outText("restart", true);
			}
		});
		butRestartD.setPreferredSize(new Dimension(130, 30));
		JButton butRestartL = new JButton("Setup Light");
		butRestartL.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				real = false; // restart
				lightMode = true;
				constants = new int[] { 1, 0, 99000000, 0 };
				outputText.setText("");
				outText("restart", true);
			}
		});
		butRestartL.setPreferredSize(new Dimension(130, 30));
		
		JCheckBox SpeedPressS = new JCheckBox("SpeedPressSensitivity", true);
		SpeedPressS.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					modeSS[0] = false;
				}
				if (e.getStateChange() == ItemEvent.SELECTED) {
					modeSS[0] = true;
				}
			}
		});
		
		JCheckBox AverageMotionS = new JCheckBox("AverageMotionSensitivity", true);
		AverageMotionS.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					modeSS[1] = false;
				}
				if (e.getStateChange() == ItemEvent.SELECTED) {
					modeSS[1] = true;
				}
			}
		});
		
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = c.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		panel3.add(butStart, c);
		c.gridx = 0;
		c.gridy = 1;
		panel3.add(butStop, c);
		c.gridx = 1;
		c.gridy = 0;
		panel3.add(butRestartD, c);
		c.gridx = 1;
		c.gridy = 1;
		panel3.add(butRestartL, c);
		c.insets = new Insets(5, 15, 5, 5);
		c.gridx = 2;
		c.gridy = 0;
		panel3.add(SpeedPressS, c);
		c.gridx = 2;
		c.gridy = 1;
		panel3.add(AverageMotionS, c);
		
		panel.add(panel1, BorderLayout.LINE_START);
		panel.add(panel2, BorderLayout.LINE_END);
		panel.add(outputText, BorderLayout.PAGE_END);
		panel.add(panel3, BorderLayout.PAGE_START);
		frame.pack();
		frame.setResizable(false);
		constants = new int[] { -1, -1, -1, 1 };
		modeSS = new boolean[] { true, true };
		frame.setVisible(true);
		running = true;
	}
	
	private void run() {
		while (running == true) {
			int[] t_ = null;
			if (modeSS[0] == true) { // modeSS[0]=true when want to not move mouse on quick press
				t_ = processSS();
			} else {
				t_ = process();
			}
			panel2.redraw(t_);
			wait(50);
		}
	}
	
	public static void main(String[] args) {
	TouchScreen instance = new TouchScreen();
		instance.run();
		System.out.println("closing");
		instance = null;
		System.exit(0);
		}
	
	private int[] process() {
		int[] t_ = getTouchValue();
		if (t_ != null) {
			if (constants[3] == 1) {
				int clickConstant = constants[0];// 11400000;
				float w = image.getWidth(panel2);
				float h = image.getHeight(panel2);
				
				int darkestValue = t_[0];
				int[] darkestLocation = new int[] { t_[1], t_[2] };
				
				// control red circle and mouse cursor
				int click = 0;
				int xc = darkestLocation[0];
				int yc = darkestLocation[1];
				float mx = screenSize.width - xc / w * screenSize.width;
				float my = yc / h * screenSize.height;
				// outText(mx + " / " + screenSize.width + " , " + my + " / " + screenSize.height, true);
				if (real) {
					robot.mouseMove((int) mx, (int) my);
				}
				// outText(darkestValue,true);
				if ((darkestValue >= clickConstant & !lightMode) | (darkestValue <= clickConstant & lightMode)) {
					// outText("click  " + darkestValue,true);
					click = 1;
					if (real & mouseDown == false) {
						mouseDown = true;
						robot.mousePress(InputEvent.BUTTON1_MASK);
					}
				} else {
					if (real) {
						mouseDown = false;
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
					}
				}
				return new int[] { (xc), (yc), click };
				
			} else {
				int waitTime = 2500;
				int d1 = 0, d2 = 0, d3 = 0;
				outText("don't touch", false);
				wait(waitTime);
				d1 = getTouchValue()[0];
				outText("  = " + d1, true);
				outText("touch lightly", false);
				wait(waitTime);
				d2 = getTouchValue()[0];
				outText("  = " + d2, true);
				outText("press hard", false);
				wait(waitTime);
				d3 = getTouchValue()[0];
				outText("  = " + d3, true);
				
				constants[0] = (d2 + d3) / 2;
				constants[2] = (d2 + d1) / 2;
				constants[1] = (d2 * 2 + d3 - d1) / 2;// d1 * -1 / 4 + d2 * 2 / 4 + d3 * 3 / 4;// (d2 * 2 + d3 - d1) / 2;
				constants[3] = 1;
			}
		} else if (real) {
			mouseDown = false;
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		return null;
	}
	
	private int[] processSS() { // SpeedSensitive
		int[] t_ = getTouchValue();
		if (t_ != null) {
			if (constants[3] == 1) {
				int clickConstant = constants[0];
				int maxDarkness = constants[1];
				int deltaDarknessSS = Math.abs((maxDarkness - clickConstant)) / 100;
				
				float w = image.getWidth(panel2);
				float h = image.getHeight(panel2);
				
				int darkestValue = t_[0];
				int[] darkestLocation = new int[] { t_[1], t_[2] };
				
				// control red circle and mouse cursor
				int click = 0;
				int xc = darkestLocation[0];
				int yc = darkestLocation[1];
				float mx = screenSize.width - xc / w * screenSize.width;
				float my = yc / h * screenSize.height;
				// outText(mx + " / " + screenSize.width + " , " + my + " / " + screenSize.height, true);
				// System.out.println((Math.abs(darkestValue - lastDarkValue) + "   /   " + deltaDarknessSS));
				if (lastDarkValue == -1 | (Math.abs(darkestValue - lastDarkValue) <= deltaDarknessSS & !lightMode) | (Math.abs(darkestValue - lastDarkValue) <= deltaDarknessSS) & lightMode) {
					if (real) {
						robot.mouseMove((int) mx, (int) my);
					}
				} else {
					xc = -1;
				}
				// outText(darkestValue,true);
				if ((darkestValue >= clickConstant & !lightMode) | (darkestValue <= clickConstant & lightMode)) {
					// outText("click  " + darkestValue,true);
					click = 1;
					if (real & mouseDown == false) {
						mouseDown = true;
						robot.mousePress(InputEvent.BUTTON1_MASK);
					}
				} else {
					if (real) {
						mouseDown = false;
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
					}
				}
				lastDarkValue = darkestValue;
				return new int[] { xc, yc, click };
				
			} else {
				int waitTime = 2500;
				int d1 = 0, d2 = 0, d3 = 0;
				outText("don't touch", false);
				wait(waitTime);
				d1 = getTouchValue()[0];
				outText("  = " + d1, true);
				outText("touch lightly", false);
				wait(waitTime);
				d2 = getTouchValue()[0];
				outText("  = " + d2, true);
				outText("press hard", false);
				wait(waitTime);
				d3 = getTouchValue()[0];
				outText("  = " + d3, true);
				
				constants[0] = (d2 + d3) / 2;
				constants[2] = (d2 + d1) / 2;
				constants[1] = (d2 * 2 + d3 - d1) / 2;// d1 * -1 / 4 + d2 * 2 / 4 + d3 * 3 / 4;// (d2 * 2 + d3 - d1) / 2;
				constants[3] = 1;
			}
		} else {
			lastDarkValue = -1;
			if (real) {
				mouseDown = false;
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}
		}
		return null;
	}
	
	private void wait(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			System.out.println("error 3");
		}
	}
	
	private void outText(String string, boolean newLine) {
		outputText.append(string);
		if (newLine) {
			outputText.append("\n");
		}
	}
	
	private int[] getDarkestValue() { // value, x, y
		Buffer buf = frameGrabber.grabFrame();
		image = (new BufferToImage((VideoFormat) buf.getFormat()).createImage(buf));
		
		int maxDarkness = constants[1];
		int minDarkness = constants[2];
		
		if (image != null) {
			float w = image.getWidth(panel2);
			float h = image.getHeight(panel2);
			
			// gray
			ImageProducer producer = new FilteredImageSource(image.getSource(), new GrayFilter(true, 50));
			Image image2 = panel2.createImage(producer);
			// image2 = image;
			
			// get pixels
			imageB = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_RGB);
			imageB.createGraphics().drawImage(image2, null, null);
			
			// find darkest pixel
			int darkestValue = 0;
			int[] darkestLocation = null;
			int dark_t = 0;
			for (int v = 0; v < w; v++) {
				for (int vv = 0; vv < h; vv++) {
					dark_t = -imageB.getRGB(v, vv);
					if (dark_t > darkestValue & dark_t < maxDarkness & dark_t > minDarkness) {
						darkestValue = dark_t;
						darkestLocation = new int[] { v, vv };
					}
				}
			}
			if (darkestLocation != null) {
				return new int[] { darkestValue, darkestLocation[0], darkestLocation[1] };
			}
		}
		return null;
	}
	
	private int[] getLightestValue() { // value, x, y
		Buffer buf = frameGrabber.grabFrame();
		image = (new BufferToImage((VideoFormat) buf.getFormat()).createImage(buf));
		
		int maxDarkness = constants[1];
		int minDarkness = constants[2];
		
		if (image != null) {
			float w = image.getWidth(panel2);
			float h = image.getHeight(panel2);
			
			// gray
			ImageProducer producer = new FilteredImageSource(image.getSource(), new GrayFilter(true, 50));
			Image image2 = panel2.createImage(producer);
			// image2 = image;
			
			// get pixels
			imageB = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_RGB);
			imageB.createGraphics().drawImage(image2, null, null);
			
			// find darkest pixel
			int darkestValue = 990000000;
			int[] darkestLocation = null;
			int dark_t = 0;
			for (int v = 0; v < w; v++) {
				for (int vv = 0; vv < h; vv++) {
					dark_t = -imageB.getRGB(v, vv);
					if (dark_t < darkestValue & dark_t > maxDarkness & dark_t < minDarkness) {
						darkestValue = dark_t;
						darkestLocation = new int[] { v, vv };
					}
				}
			}
			lastDarkLocation = darkestLocation;
			if (darkestLocation != null) {
				return new int[] { darkestValue, darkestLocation[0], darkestLocation[1] };
			}
		}
		return null;
	}
	
	private int[] getTouchValue() {
		if (lightMode) {
			return getLightestValue();
		} else {
			return getDarkestValue();
		}
	}
	
}
