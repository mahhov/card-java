package com.alderstone.multitouch.mac.touchpad;

import java.util.Observable;

public class TouchpadObservable extends Observable {
	
	private static volatile Object initGuard;
	private static volatile boolean loaded = false;
	private static final TouchpadObservable INSTANCE = new TouchpadObservable();
	
	// diasable client construction
	private TouchpadObservable() {
	}
	
	public static TouchpadObservable getInstance() {
		startupNative();
		return INSTANCE;
	}
	
	static {
		initGuard = new Object();
		System.loadLibrary("GlulogicMT");
	}
	
	private static void startupNative() {
		synchronized (initGuard) {
			if (!loaded) {
				loaded = true;
				registerListener();
				ShutdownHook shutdownHook = new ShutdownHook();
				Runtime.getRuntime().addShutdownHook(shutdownHook);
			}
		}
	}
	
	private static void shutdownNative() {
		deregisterListener();
	}
	
	public static void mtcallback(int frame, double timestamp, int id, int state, float size, float x, float y, float dx, float dy, float angle, float majorAxis, float minorAxis) {
		INSTANCE.update(frame, timestamp, id, state, size, x, y, dx, dy, angle, majorAxis, minorAxis);
	}
	
	// native methods
	
	native static int registerListener();
	
	native static int deregisterListener();
	
	// shutdown hook
	static class ShutdownHook extends Thread {
		public void run() {
			shutdownNative();
		}
	}
	
	
	// Observer interface code
	
	public void update(int frame, double timestamp, int id, int state, float size, float x, float y, float dx, float dy, float angle, float majorAxis, float minorAxis) {
		setChanged();
		notifyObservers(new Finger(frame, timestamp, id, state, size, x, y, dx, dy, angle, majorAxis, minorAxis));
	}
	
	
}
