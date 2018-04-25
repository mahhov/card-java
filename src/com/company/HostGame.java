package com.company;

class HostGame {
	
	Painter painter;
	Board board;
	Controller controller;
	boolean running;
	
	HostGame() {
		controller = new Controller();
		painter = new Painter(controller);
		board = new Board();
		loopRun();
		System.exit(0);
	}
	
	void loopRun() {
		running = true;
		
		while (running) {
			handleInput();
			painter.clearDraw(board.active.top);
			board.draw(painter.brush, controller.mouseCord);
			board.update(controller.getClick());
			painter.repaint();
			
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	void handleInput() {
		if (controller.windowClosed)
			running = false;
		
		
		
	}
	
	public static void main(String[] args) {
		new HostGame();
	}
}
