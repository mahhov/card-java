package com.alderstone.multitouch.mac.touchpad.tests;

import com.alderstone.multitouch.mac.touchpad.Finger;
import com.alderstone.multitouch.mac.touchpad.FingerState;
import com.alderstone.multitouch.mac.touchpad.TouchpadObservable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

public class ControlTest implements Observer {
	
	Controller c;
	TouchpadObservable tpo;
	FingerList fl;
	Finger f;
	
	boolean running;
	int count;
	
	public ControlTest() {
		c = new Controller();
		fl = new FingerList(200);
		tpo = TouchpadObservable.getInstance();
		tpo.addObserver(this);
	}
	
	void run() {
		running = true;
		System.out.println("begin");
		
		int n = 0;
		while (true) {
			Finger f = this.f;
			if (f == null)
				if (!fl.isEmpty()) {
					c.initGlide();
					c.initScrollGlide();
					c.initHScrollGlide();
					fl.clear();
				} else {
					c.updateGlide();
					c.updateScrollGlide();
					c.updateHScrollGlide();
				}
			else {
				c.checkScroll();
				fl.add(f, c.scrollCenterWeight);
			}
			try {
				Thread.sleep(10);
				if (n++ == 400) {
					n = 0;
					c.resetScreens();
					System.out.println("reset screens");
				}
			} catch (Exception e) {
			}
		}
	}
	
	public void update(Observable o, Object arg) {
		Finger fnext = (Finger) arg;
		FingerState state = fnext.getState();
		if (state == FingerState.TAP) {
			count++;
			if (count == 5)
				running = !running;
		} else if (state == FingerState.RELEASED)
			count--;
		
		
		if (f != null && f.getID() != fnext.getID())
			return;
		
		if (state == FingerState.PRESSED || state == FingerState.TAP)
			f = fnext;
		else if (state == FingerState.RELEASED)
			f = null;
	}
	
	public static void main(String[] arg) {
		while (true) {
			try {
				new ControlTest().run();
			} catch (Exception e) {
			}
		}
	}
	
	class FingerList {
		Finger f[];
		int size, maxSize, cursor;
		float centerx, centery;
		
		FingerList(int maxSize) {
			this.maxSize = maxSize;
			f = new Finger[maxSize];
			cursor = -1;
		}
		
		void clear() {
			size = 0;
			cursor = -1;
		}
		
		boolean isEmpty() {
			return size == 0;
		}
		
		void add(Finger f, float weight) {
			centerx = (centerx * weight + f.getX()) / (weight + 1);
			centery = (centery * weight + f.getY()) / (weight + 1);
			
			if (size < maxSize)
				size++;
			if (++cursor == maxSize)
				cursor = 0;
			this.f[cursor] = f;
		}
		
		Finger getLast() {
			if (cursor == -1)
				return null;
			return f[cursor];
		}
		
		Finger getHistory(int time) {
			if (size == 0)
				return null;
			if (time >= size)
				time = size - 1;
			int c = cursor - time;
			if (c < 0)
				c += maxSize;
			return f[c];
		}
		
		float[] getMovement(int time) {
			Finger history = getHistory(time);
			if (history == null)
				return null;
			return new float[] {f[cursor].getX() - history.getX(), -f[cursor].getY() + history.getY()};
			
		}
		
		// bad, don't use, jerky velocitiess
		float[] getMovement() {
			return new float[] {f[cursor].getXVelocity() / 100, -f[cursor].getYVelocity() / 100};
		}
		
		float getRotation(int time, float scrollCenterShiftX, float scrollCenterShiftY) {
			Finger history = getHistory(time);
			float centerx = this.centerx - scrollCenterShiftX;
			float centery = this.centery + scrollCenterShiftY;
			return (history.getY() - centery) * (f[cursor].getX() - centerx) - (history.getX() - centerx) * (f[cursor].getY() - centery);
		}
		
		void setCenter(float x, float y) {
			centerx = x;
			centery = y;
		}
	}
	
	class Controller {
		Rectangle[] bound;
		int currentBound;
		
		Robot r;
		float x, y, vx, vy;
		boolean gliding;
		final int history = 5;
		final float friction = 0.92f, minVSq = 0.0001f, minStartVSq = 0.01f, vscale = 500;
		
		boolean scrolling, scrollGliding, hscrolling, hscrollGliding;
		float scrollCumulate, scrollVelocity, hscrollCumulate, hscrollVelocity;
		int scrollSetX, scrollSetY, scrollHistory = 1;
		final float scrollRegion = 0.08f, scrollScale = 25, scrollFriction = 0.9f, minScrollVelocity = 0.01f, scrollVelocityScale = 3, scrollCenterWeight = 20f, scrollCenterShift = 0.01f, scrollVelocityWeight = 2;
		
		Controller() {
			resetScreens();
			
			try {
				r = new Robot();
			} catch (AWTException e) {
				System.out.println("unable to create robot");
				return;
			}
		}
		
		void resetScreens() {
			currentBound = -1;
			GraphicsDevice currentDevice = MouseInfo.getPointerInfo().getDevice();
			GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			bound = new Rectangle[gs.length];
			for (int i = 0; i < gs.length; i++) {
				if (currentDevice == gs[i])
					currentBound = i;
				bound[i] = gs[i].getConfigurations()[0].getBounds();
			}
			if (currentBound == -1)
				System.out.println("bad");
		}
		
		void printXMovementHistory() {
			int c = fl.cursor;
			int done = fl.size;
			while (done > 1) {
				done--;
				c--;
				float dx;
				if (c != -1)
					dx = (fl.f[c + 1].getX() - fl.f[c].getX());
				else {
					c += fl.maxSize;
					dx = (fl.f[0].getX() - fl.f[c].getX());
				}
				dx = (int) (dx * 1000);
				System.out.print(dx + ", ");
			}
			System.out.println("\n new, old");
		}
		
		void initGlide() {
			gliding = false;
			Point p = getMouse();
			
			if (scrolling || hscrolling || p == null)
				return;
			
			x = p.x;
			y = p.y;
			float[] movement = fl.getMovement(10);
			float[] movementShort = fl.getMovement(5);
			if (movementShort == null || movement == null)
				return;
			
			vx = movementShort[0] * vscale;
			vy = movementShort[1] * vscale;
			
			if (vx * vx + vy * vy > minStartVSq * vscale)
				gliding = true;
			
			vx = movement[0] * vscale;
			vy = movement[1] * vscale;
			
			updateGlide();
		}
		
		void updateGlide() {
			if (!gliding)
				return;
			x += vx;
			y += vy;
			bound();
			vx *= friction;
			vy *= friction;
			if (vx * vx + vy * vy < minVSq)
				gliding = false;
			moveMouse((int) x, (int) y);
		}
		
		void checkScroll() {
			if (fl.isEmpty()) {
				scrollGliding = false;
				hscrollGliding = false;
				initScroll();
				initHScroll();
				
			} else if (scrolling)
				updateScroll();
			else if (hscrolling)
				updateHScroll();
		}
		
		void initScroll() {
			if (f.getX() > 1 - scrollRegion) {
				scrolling = true;
				hscrolling = false;
				r.keyRelease(KeyEvent.VK_SHIFT);
				fl.setCenter(f.getX() - scrollRegion / 2, f.getY());
				scrollCumulate = 0;
				Point p = getMouse();
				if (p != null) {
					scrollSetX = (int) p.getX();
					scrollSetY = (int) p.getY();
				}
			} else
				scrolling = false;
		}
		
		void updateScroll() {
			if (f.getX() < 1 - scrollRegion * 4) {
				scrolling = false;
				return;
			}
			
			float rotation = fl.getRotation(scrollHistory, scrollCenterShift, 0);
			if (rotation != 0) {
				float[] movement = fl.getMovement(history);
				float distSq = 0;
				if (rotation * scrollVelocity < 0 || (scrollVelocity < minScrollVelocity * 2 && scrollVelocity > -minScrollVelocity * 2)) {
					distSq = (float) Math.sqrt(movement[0] * movement[0] + movement[1] * movement[1]) * scrollScale;
					if (rotation > 0)
						distSq = -distSq;
					scroll(distSq);
				}
				scrollVelocity = (scrollVelocity * scrollVelocityWeight + distSq) / (scrollVelocityWeight + 1);
				moveMouse(scrollSetX, scrollSetY);
			}
		}
		
		void initHScroll() {
			if (f.getY() < scrollRegion && !scrolling) {
				hscrolling = true;
				r.keyPress(KeyEvent.VK_SHIFT);
				fl.setCenter(f.getX(), f.getY() + scrollRegion / 2);
				hscrollCumulate = 0;
				Point p = getMouse();
				if (p != null) {
					scrollSetX = (int) p.getX();
					scrollSetY = (int) p.getY();
				}
			} else {
				hscrolling = false;
				r.keyRelease(KeyEvent.VK_SHIFT);
			}
		}
		
		void updateHScroll() {
			if (f.getY() > scrollRegion * 4) {
				hscrolling = false;
				r.keyRelease(KeyEvent.VK_SHIFT);
				return;
			}
			
			float rotation = fl.getRotation(scrollHistory, 0, scrollCenterShift);
			if (rotation != 0) {
				float[] movement = fl.getMovement(history);
				float distSq = 0;
				if (rotation * hscrollVelocity < 0 || (hscrollVelocity < minScrollVelocity * 2 && hscrollVelocity > -minScrollVelocity * 2)) {
					distSq = (float) Math.sqrt(movement[0] * movement[0] + movement[1] * movement[1]) * scrollScale;
					if (rotation < 0)
						distSq = -distSq;
					hscroll(distSq);
				}
				hscrollVelocity = (hscrollVelocity * scrollVelocityWeight + distSq) / (scrollVelocityWeight + 1);
				moveMouse(scrollSetX, scrollSetY);
			}
		}
		
		void initScrollGlide() {
			if (!scrolling)
				return;
			
			scrolling = false;
			scrollVelocity *= scrollVelocityScale;
			scrollGliding = true;
		}
		
		void initHScrollGlide() {
			if (!hscrolling)
				return;
			
			hscrolling = false;
			hscrollVelocity *= scrollVelocityScale;
			hscrollGliding = true;
		}
		
		void updateScrollGlide() {
			if (!scrollGliding)
				return;
			
			scrollVelocity *= scrollFriction;
			if (scrollVelocity < minScrollVelocity && scrollVelocity > -minScrollVelocity)
				scrollGliding = false;
			else
				scroll(scrollVelocity);
		}
		
		void updateHScrollGlide() {
			if (!hscrollGliding)
				return;
			
			hscrollVelocity *= scrollFriction;
			if (hscrollVelocity < minScrollVelocity && hscrollVelocity > -minScrollVelocity) {
				hscrollGliding = false;
				r.keyRelease(KeyEvent.VK_SHIFT);
			} else
				hscroll(hscrollVelocity);
		}
		
		boolean isBound(int minx, int width, int miny, int height) {
			int maxx = minx + width;
			int maxy = miny + height;
			if (x < minx)
				return false;
			else if (x > maxx)
				return false;
			if (y < miny)
				return false;
			else if (y > maxy)
				return false;
			return true;
		}
		
		void bound(int minx, int width, int miny, int height) {
			int maxx = minx + width;
			int maxy = miny + height;
			if (x < minx) {
				x = minx;
				gliding = false;
			} else if (x > maxx) {
				x = maxx;
				gliding = false;
			}
			if (y < miny) {
				y = miny;
				gliding = false;
			} else if (y > maxy) {
				y = maxy;
				gliding = false;
			}
		}
		
		void bound() {
			if (!isBound((int) bound[currentBound].getX(), (int) bound[currentBound].getWidth(), (int) bound[currentBound].getY(), (int) bound[currentBound].getHeight())) {
				System.out.println("moved out of bounds");
				for (int i = 0; i < bound.length; i++)
					if (i != currentBound && isBound((int) bound[i].getX(), (int) bound[i].getWidth(), (int) bound[i].getY(), (int) bound[i].getHeight())) {
						System.out.println("changed to bounds : " + i);
						currentBound = i;
						break;
					}
			}
			bound((int) bound[currentBound].getX(), (int) bound[currentBound].getWidth(), (int) bound[currentBound].getY(), (int) bound[currentBound].getHeight());
		}
		
		void moveMouse(int x, int y) {
			r.mouseMove((int) (x + 0.5f), (int) (y + 0.5f));
		}
		
		Point getMouse() {
			PointerInfo pointer = MouseInfo.getPointerInfo();
			if (pointer == null)
				return null;
			return pointer.getLocation();
		}
		
		void scroll(float amount) {
			scrollCumulate += amount;
			int scroll = (int) scrollCumulate;
			scrollCumulate -= scroll;
			r.mouseWheel(scroll);
		}
		
		void hscroll(float amount) {
			hscrollCumulate += amount * 10;
			int scroll = (int) hscrollCumulate;
			hscrollCumulate -= scroll;
			r.mouseWheel(scroll);
		}
		
	}
}