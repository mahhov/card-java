package com.alderstone.multitouch.mac.touchpad.tests;

import java.awt.*;
import java.awt.event.AWTEventListener;

public class Y {
	
	public static void main(String[] arg) {
		long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK
				+ AWTEvent.MOUSE_EVENT_MASK
				+ AWTEvent.KEY_EVENT_MASK;
		
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent e) {
				System.out.println(e.getID());
			}
		}, eventMask);
		
		try {
			while (true) {
				Thread.sleep(100);
			}
		} catch (Exception e) {
		}
	}
}
