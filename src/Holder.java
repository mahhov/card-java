//import engine.JavaPainter;
//
//import java.awt.*;
//
//public class Holder {
//
//	public static void main(String args[]) {
//		long lastTime = 0, time = 0; // System.nanoTime();
//		int frame = 0;
//		JavaPainter jp = new JavaPainter(800);
//		while (true) {
//			time = System.nanoTime();
//			if (time - lastTime > 1000000000l) {
//				System.out.println(frame);
//				frame = 0;
//				lastTime = time;
//			}
//			frame++;
//
//			jp.clearDraw();
//			for (int i = 0; i < 10000; i++) {
//				jp.brush.setColor(Color.red);
//				int x = (int) (Math.random() * 800);
//				int y = (int) (Math.random() * 800);
//				int w = (int) (Math.random() * 100);
//				int h = (int) (Math.random() * 100);
//				jp.brush.fillRect(x, y, w, h);
//			}
//			jp.repaint();
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//}
