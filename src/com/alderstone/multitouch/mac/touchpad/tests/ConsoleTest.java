package com.alderstone.multitouch.mac.touchpad.tests;

import com.alderstone.multitouch.mac.touchpad.Finger;
import com.alderstone.multitouch.mac.touchpad.FingerState;
import com.alderstone.multitouch.mac.touchpad.TouchpadObservable;

import java.util.Observable;
import java.util.Observer;

public class ConsoleTest implements Observer {
	
	// Class that is resposible for registering with the Trackpad
	// and notifies registered clients of Touchpad Multitouch Events
	TouchpadObservable tpo;
	
	// Touchpad Multitouch updateGlide event handler, called on single MT Finger event
	
	public void update(Observable obj, Object arg) {
		
		// The event 'arg' is of type: com.alderstone.multitouch.mac.touchpad.Finger
		Finger f = (Finger) arg;
		
		int frame = f.getFrame();
		double timestamp = f.getTimestamp();
		int id = f.getID();
		FingerState state = f.getState();
		float size = f.getSize();
		float angRad = f.getAngleInRadians();
		int angle = f.getAngle();            // return in Degrees
		float majorAxis = f.getMajorAxis();
		float minorAxis = f.getMinorAxis();
		float x = f.getX();
		float y = f.getY();
		float dx = f.getXVelocity();
		float dy = f.getYVelocity();
		
		System.out.println("frame=" + frame + "\ttimestamp=" + timestamp + "\tid=" + id + "\tstate=" + state + "\tsize=" + size + "\tx,y=(" + x + "," + y + ")\tdx,dy=(" + dx + "," + dy + ")\t" + "angle=" + angle + "majAxis=" + majorAxis + "\tminAxis=" + minorAxis);
	}
	
	public void run() {
		tpo = TouchpadObservable.getInstance();
		tpo.addObserver(this);
	}
	
	public static void main(String[] args) {
		
		ConsoleTest ct = new ConsoleTest();
		ct.run();
		System.out.println("CTRL-C to exit.");
		try {
			while (true) {
				Thread.sleep(5000);
			}
		} catch (Exception e) {
		}
	}
}

