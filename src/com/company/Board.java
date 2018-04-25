package com.company;

import java.awt.*;

class Board {
	Player human;
	Player cpu;
	Player active, inactive;
	
	boolean humanTurn;
	
	Board() {
		humanTurn = Math.random() < 0.5 ? true : false;
		human = new Player(300, false);
		cpu = new Cpu(300, true);
		if (humanTurn) {
			human.beginTurn(0);
			active = human;
			inactive = cpu;
		} else {
			cpu.beginTurn(0);
			active = cpu;
			inactive = human;
		}
	}
	
	void draw(Graphics2D brush, Controller.Cord cord) {
		human.draw(brush, humanTurn ? cord : null);
		cpu.draw(brush, !humanTurn ? cord : null);
	}
	
	void update(int mouseClick) {
		active.update(mouseClick);
		
		if (active.selectingBlock) {
			if (active.currentBlock == -1) {
				// do dmamage
				
			} else if (active.currentBlock != -2)
				;// do block
			
		} else if (active.requestEndTurn) {
			active.nextOffensive();
			flipTurn();
			active.beginDeffensive();
		}
	}
	
	void flipTurn() {
		humanTurn = !humanTurn;
		Player t = active;
		active = inactive;
		inactive = t;
	}
	
	//	Player getActive() {
	//		if (humanTurn)
	//			return human;
	//		else
	//			return cpu;
	//	}
}
