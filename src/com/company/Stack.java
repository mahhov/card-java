package com.company;

import java.awt.*;

abstract class Stack {
	Card card[];
	int size;
	int x, y;
	
	Stack(int x, int y, int maxSize) {
		this.x = x;
		this.y = y;
		size = 0;
		card = new Card[maxSize];
	}
	
	void addCard(Card c) {
		card[size++] = c;
	}
	
	Card removeCard(int index) {
		Card c = card[index];
		for (int i = index; i < size - 1; i++)
			card[i] = card[i + 1];
		size--;
		return c;
	}
	
	void removeCard(int index1, int index2) {
		removeCard(index1);
		if (index1 < index2)
			removeCard(index2 - 1);
		else
			removeCard(index2);
	}
	
	// unused
	void joinStack(Stack s) {
		for (Card c : s.card)
			card[size++] = c;
	}
	
	void untapAll() {
		for (int i = 0; i < size; i++)
			card[i].tapped = false;
	}
	
	int draw(Graphics2D brush, Controller.Cord cord) {
		if (size > 0)
			card[0].draw(x, y, 0, brush, cord);
		return -1;
	}
	
	// unused
	boolean isMouseOver(Controller.Cord cord) {
		if (cord.x < Card.getX(x, 0, 0) || cord.x > Card.getX(x, size - 1, 0) + Card.WIDTH)
			return false;
		int cardy = Card.getY(y, 0);
		if (cord.y < cardy || cord.y > cardy + Card.HEIGHT)
			return false;
		
		for (int i = 0; i < size; i++)
			if (card[i].isMouseOver(cord, x, y, i, 0))
				return true;
		return false;
	}
}

class PillarStack extends Stack {
	PillarStack(int x, int y) {
		super(x, y, 3);
	}
	
	int draw(Graphics2D brush, Controller.Cord cord) {
		int r = -1;
		for (int i = 0; i < size; i++)
			if (card[i].draw(x, y, i, brush, cord))
				r = i;
		return r;
	}
	
	void addCard(Card c) {
		int index = 0;
		while (index < size && c.number > card[index].number)
			index++;
		for (int i = size++; i > index; i--)
			card[i] = card[i - 1];
		card[index] = c;
	}
	
	int replacePillar(Card c) {
		int num = getPillarNumber(c);
		for (int i = 0; i < size; i++)
			if (getPillarNumber(card[i]) == num)
				if (!card[i].isPillar())
					return i; // valid, replace i
				else
					return -2; // not valid
		return -1; // valid, don't replace
	}
	
	int untapped() {
		int sum = 0, num;
		for (int i = 0; i < size; i++)
			if (!card[i].tapped)
				sum += getPillarNumber(card[i]);
		return sum;
	}
	
	void tap(int cost) {
		// check to afford with 1 tap
		for (int i = 0; i < size; i++)
			if (!card[i].tapped && cost <= getPillarNumber(card[i])) {
				card[i].tapped = true;
				return;
			}
		
		// tap largest untapped card
		for (int i = size - 1; i >= 0; i--)
			if (!card[i].tapped) {
				card[i].tapped = true;
				cost -= getPillarNumber(card[i]);
				break;
			}
		
		// check to afford with 2 taps
		for (int i = 0; i < size; i++)
			if (!card[i].tapped && cost <= getPillarNumber(card[i])) {
				card[i].tapped = true;
				return;
			}
		
		// check to afford with 3 taps
		for (int i = 0; i < size; i++)
			card[i].tapped = true;
	}
	
	int getPillarNumber(Card c) {
		if (c.number >= 9)
			return 9;
		else if (c.number >= 6)
			return 6;
		else
			return 3;
	}
}

class HandStack extends Stack {
	HandStack(int x, int y) {
		super(x, y, Player.MAX_DECK + 1);
	}
	
	int draw(Graphics2D brush, Controller.Cord cord, int selected) {
		int r = -1;
		for (int i = 0; i < size; i++)
			if (i == selected) {
				// int slotx, int sloty, int slotzLarge, int slotw, Graphics2D brush, Controller.Cord cord
				if (card[i].draw(x + i, y, 0, 1, brush, cord))
					r = i;
			} else if (card[i].draw(x + i, y, 0, brush, cord))
				r = i;
		
		for (int i = size; i < Player.MAX_DECK; i++)
			Card.draw(x + i, y, Painter.BACK_COLOR, Color.LIGHT_GRAY, brush);
		
		return r;
	}
	
	void pullHand(DeckStack deck, int num) {
		for (int i = 0; i < num; i++)
			addCard(deck.drawTopCard());
		
	}
	
	void selectDiscard() {
		for (int i = 0; i < size; i++)
			card[i].redBorder = true;
	}
	
	void endSelectDiscard(int discard) {
		removeCard(discard);
		for (int i = 0; i < size; i++)
			card[i].redBorder = false;
	}
	
	// unused
	boolean isMouseOver(Controller.Cord cord) {
		if (cord.x < Card.getX(x, 0, 0) || cord.x > Card.getX(x + size - 1, 0, 0) + Card.WIDTH)
			return false;
		int cardy = Card.getY(y, 0);
		if (cord.y < cardy || cord.y > cardy + Card.HEIGHT)
			return false;
		
		for (int i = 0; i < size; i++)
			if (card[i].isMouseOver(cord, x + i, y, 0, 0))
				return true;
		return false;
	}
}

class DeckStack extends Stack {
	final static int INIT_DECK_SIZE = (13 + 6) * 4;
	
	DeckStack(int x, int y) {
		super(x, y, INIT_DECK_SIZE);
	}
	
	int draw(Graphics2D brush, Controller.Cord cord) {
		boolean highlight = false;
		for (int i = 0; i < (size + 4) / 5; i++)
			highlight = highlight || card[i].draw(x, y, 0, i, 0, true, size + "", false, brush, cord);
		
		if (highlight) {
			for (int i = 0; i < (size + 4) / 5; i++)
				card[i].draw(x, y, 0, i, 0, true, size + "", true, brush, cord);
			return 0;
		}
		
		return -1;
	}
	
	void fillDeck() {
		// clean deck
		int f[] = new int[INIT_DECK_SIZE];
		for (int i = 0; i < INIT_DECK_SIZE; i++)
			f[i] = i;
		
		// shuffle
		int pick, t;
		for (int i = INIT_DECK_SIZE - 1; i > 0; i--) {
			pick = (int) (Math.random() * i);
			t = f[i];
			f[i] = f[pick];
			f[pick] = t;
		}
		
		// parse
		int c, number, suit;
		for (int i = 0; i < INIT_DECK_SIZE; i++) {
			c = f[i];
			suit = c / 19;
			number = c - suit * 19;
			if (number > 12) {
				if (number == 13)
					number = 1;
				else if (number < 16)
					number -= 11; // 4, 5
				else if (number < 18)
					number -= 10; // 7, 8
				else
					number = 9; // 10
			}
			// number = (((int) (Math.random() * 3)) + 1) * 3 - 1; // pillars only
			if (number < 10 && number != 0) // remove casts
				addCard(new Card(number + 1, suit));
		}
	}
	
	Card drawTopCard() {
		return card[--size];
	}
	
	// unused
	boolean isMouseOver(Controller.Cord cord) {
		if (cord.x < Card.getX(x, 0, 0) || cord.x > Card.getX(x, 0, size - 1) + Card.WIDTH)
			return false;
		int cardy = Card.getY(y, 0);
		if (cord.y < cardy || cord.y > cardy + Card.HEIGHT)
			return false;
		
		for (int i = 0; i < size; i++)
			if (card[i].isMouseOver(cord, x, y, 0, i))
				return true;
		return false;
	}
}

class CreatureStack extends Stack {
	CreatureStack(int x, int y) {
		super(x, y, 2);
	}
	
	boolean tapped;
	
	int draw(Graphics2D brush, Controller.Cord cord) {
		int tapy = tapped ? 2 : 0;
		int r = -1;
		for (int i = 0; i < size; i++)
			if (card[i].draw(x, y, i, tapy, brush, cord))
				r = i;
		return r;
	}
	
	int offense() {
		return card[0].number;
	}
	
	int defense() {
		return card[1].number;
	}
}
