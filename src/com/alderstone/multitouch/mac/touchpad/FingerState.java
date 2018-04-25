package com.alderstone.multitouch.mac.touchpad;

public class FingerState {
	
	private final String name;
	
	private FingerState(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public static final FingerState PRESSED = new FingerState("PRESSED");
	public static final FingerState RELEASED = new FingerState("RELEASED");
	public static final FingerState HOVER = new FingerState("HOVER");
	public static final FingerState PRESSING = new FingerState("PRESSING");
	public static final FingerState RELEASING = new FingerState("RELEASING");
	public static final FingerState TAP = new FingerState("TAP");
	public static final FingerState UNKNOWN_1 = new FingerState("UNKNOWN_1");
	public static final FingerState UNKNOWN = new FingerState("UNKNOWN_?");
	
	public static FingerState getStateFor(int stateId) {
		FingerState state;
		switch (stateId) {
			case 1:
				state = FingerState.UNKNOWN_1;
				break;
			case 2:
				state = FingerState.HOVER;
				break;
			case 3:
				state = FingerState.TAP;
				break;
			case 4:
				state = FingerState.PRESSED;
				break;
			case 5:
				state = FingerState.PRESSING;
				break;
			case 6:
				state = FingerState.RELEASING;
				break;
			case 7:
				state = FingerState.RELEASED;
				break;
			default:
				state = FingerState.UNKNOWN;
				break;
		}
		
		return state;
	}
}
