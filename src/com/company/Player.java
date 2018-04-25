package com.company;

import java.awt.*;

class Player {
	static final int MAX_DECK = 10;
	
	static final int MOUSE_NONE = 0, MOUSE_DECK = 1, MOUSE_HAND = 2, MOUSE_PILLAR = 3, MOUSE_CREATURE = 4;
	int mouseStack, mouseIndex; // turn state
	
	// player state
	boolean top;
	DeckStack deck;
	HandStack hand;
	PillarStack pillar[];
	CreatureStack creature[];
	int numCreatures;
	final int maxLife;
	int life;
	
	// turn state
	boolean pillarPlayed;
	boolean requestEndTurn;
	int energyCostHint;
	boolean selectingDiscard, selectingBlock;
	int handCreatureSelected, handCastSelected;
	int currentOffensive, currentBlock;
	
	Player(int life, boolean top) {
		this.top = top;
		handCreatureSelected = -1;
		handCastSelected = -1;
		energyCostHint = -1;
		pillar = new PillarStack[4];
		creature = new CreatureStack[24];
		
		if (top) {
			deck = new DeckStack(0, 0);
			hand = new HandStack(2, 0);
			for (int i = 0; i < pillar.length; i++)
				pillar[i] = new PillarStack(2 + i * 2, 1);
			for (int i = 0; i < creature.length; i++)
				creature[i] = new CreatureStack(i * 2, 2);
		} else {
			deck = new DeckStack(0, 7);
			hand = new HandStack(2, 7);
			for (int i = 0; i < pillar.length; i++)
				pillar[i] = new PillarStack(2 + i * 2, 6);
			for (int i = 0; i < creature.length; i++)
				creature[i] = new CreatureStack(i * 2, 5);
		}
		
		deck.fillDeck();
		hand.pullHand(deck, 6);
		this.life = life;
		maxLife = life;
		mouseStack = MOUSE_NONE;
		currentOffensive = -1;
	}
	
	void draw(Graphics2D brush, Controller.Cord cord) {
		mouseStack = MOUSE_NONE;
		
		int click = deck.draw(brush, cord);
		if (click != -1) {
			mouseStack = MOUSE_DECK;
			mouseIndex = click;
		}
		
		click = hand.draw(brush, cord, handCreatureSelected != -1 ? handCreatureSelected : handCastSelected);
		if (click != -1) {
			mouseStack = MOUSE_HAND;
			mouseIndex = click;
		}
		
		for (int i = 0; i < pillar.length; i++)
			if (pillar[i].draw(brush, cord) != -1) {
				mouseStack = MOUSE_PILLAR;
				mouseIndex = i;
			}
		
		for (int i = 0; i < creature.length; i++)
			if (creature[i].draw(brush, cord) != -1) {
				mouseStack = MOUSE_CREATURE;
				mouseIndex = i;
			}
		
		// draw life
		int y;
		if (top)
			y = Painter.HEIGHT / 3 + Painter.borderSize;
		else
			y = Painter.HEIGHT * 2 / 3 + Painter.borderSize;
		brush.setColor(Color.BLACK);
		brush.drawString("Life: " + life + " / " + maxLife, 10, y);
		// draw energyCostHint
		if (energyCostHint != -1)
			brush.drawString("Cost: " + energyCostHint, 10, y + 15);
		
	}
	
	void beginTurn(int d) {
		life -= d;
		
		pillarPlayed = false;
		requestEndTurn = false;
		
		// untap all pillars
		for (int i = 0; i < pillar.length; i++)
			pillar[i].untapAll();
		// untap all creatures
		for (int i = 0; i < creature.length; i++)
			creature[i].tapped = false;
		
		handCreatureSelected = -1;
		handCastSelected = -1;
		hand.pullHand(deck, 1);
		if (hand.size > MAX_DECK) {
			selectingDiscard = true;
			hand.selectDiscard();
		} else
			selectingDiscard = false;
		energyCostHint = -1;
	}
	
	void beginDeffensive() {
		selectingBlock = true;
		currentBlock = -2;
	}
	
	int nextOffensive() {
		while (++currentOffensive < numCreatures && creature[currentOffensive].tapped)
			;
		if (currentOffensive == numCreatures)
			currentOffensive = -1;
		return currentOffensive;
	}
	
	//	int getDamage() {
	//		int damage = 0;
	//		for (int i = 0; i < creature.length; i++)
	//			if (creature[i].tapped) {
	//				damage += creature[i].offense();
	//			}
	//		return damage;
	//	}
	
	//	int getOffensiveCount() {
	//		int count = 0;
	//		for (int i = 0; i < creature.length; i++)
	//			if (creature[i].tapped)
	//				count++;
	//		return count;
	//	}
	
	void update(int mouseClick) {
		if (mouseClick != Controller.CLICK_PRESSED) {
			energyCostHint = -1;
			if (handCreatureSelected != -1 && mouseStack == MOUSE_HAND && hand.card[mouseIndex].isCreature() && validCreature(handCreatureSelected, mouseIndex))
				energyCostHint = getEnergyCost(hand.card[handCreatureSelected], hand.card[mouseIndex]);
			else if (mouseStack == MOUSE_CREATURE)
				energyCostHint = getEnergyCost(creature[mouseIndex].card[0], creature[mouseIndex].card[1]);
			return;
		}
		
		currentBlock = -1;
		boolean newHandCreatureSelected = false, newHandCastSelected = false;
		switch (mouseStack) {
			case MOUSE_DECK:
				// end turn
				if (!selectingDiscard && !selectingBlock)
					requestEndTurn = true;
				break;
			case MOUSE_HAND:
				if (selectingBlock)
					;
				else if (selectingDiscard) // discard
					discardHand(mouseIndex);
				else if (hand.card[mouseIndex].isPillar()) // play pillar
					playPillar(mouseIndex);
				else if (hand.card[mouseIndex].isCreature()) { // play creature
					if (handCreatureSelected != -1)
						if (handCreatureSelected != mouseIndex)
							playCreature(handCreatureSelected, mouseIndex);
						else
							playPillar(handCreatureSelected);
					else {
						handCreatureSelected = mouseIndex;
						newHandCreatureSelected = true;
					}
				} else { // play cast
					if (handCastSelected != mouseIndex) {
						handCastSelected = mouseIndex;
						newHandCastSelected = true;
					}
				}
				break;
			case MOUSE_PILLAR:
				// ignore
				break;
			case MOUSE_CREATURE:
				if (selectingBlock) // defend
					if (!creature[mouseIndex].tapped)
						currentBlock = mouseIndex;
					else // attack
						offenseCreature(mouseIndex);
				break;
		}
		
		if (!newHandCreatureSelected)
			handCreatureSelected = -1;
		if (!newHandCastSelected)
			handCastSelected = -1;
	}
	
	void discardHand(int i) {
		hand.endSelectDiscard(i);
		selectingDiscard = false;
	}
	
	void playPillar(int i) {
		if (!pillarPlayed && validPillar(i)) {
			Card pillarCard = hand.card[i];
			int oldPillar = pillar[pillarCard.suit].replacePillar(pillarCard);
			if (oldPillar >= 0)
				hand.addCard(pillar[pillarCard.suit].removeCard(oldPillar));
			if (oldPillar != -2) {
				hand.removeCard(i);
				pillar[pillarCard.suit].addCard(pillarCard);
				pillarPlayed = true;
			}
		}
	}
	
	void offenseCreature(int c) {
		if (!creature[c].tapped && tapPillars(creature[c].card[0].suit, getEnergyCost(creature[c].card[0], creature[c].card[1])))
			creature[c].tapped = true;
	}
	
	void playCreature(int hand1, int hand2) {
		// check combination validity
		if (!validCreature(hand1, hand2))
			return;
		
		Card card1 = hand.card[hand1];
		Card card2 = hand.card[hand2];
		
		// check cost
		if (!tapPillars(card1.suit, getEnergyCost(card1, card2)))
			return;
		
		// play card
		hand.removeCard(hand1, hand2);
		creature[numCreatures].addCard(card1);
		creature[numCreatures++].addCard(card2);
	}
	
	boolean validPillar(int h) {
		int num = hand.card[h].number;
		return num >= 3 && num <= 10;
	}
	
	boolean validCreature(int hand1, int hand2) {
		return hand.card[hand1].suit == hand.card[hand2].suit;
	}
	
	int getEnergyCost(Card c1, Card c2) {
		int r;
		if (c1.number > c2.number)
			r = (c1.number * 2 + c2.number - 8) / 2;
		else
			r = (c1.number + c2.number * 2 - 8) / 2;
		if (r < 1)
			return 1;
		if (r > 18)
			return 18;
		return r;
	}
	
	boolean canAffordEnergyCost(int suit, int cost) {
		return pillar[suit].untapped() >= cost;
	}
	
	boolean tapPillars(int suit, int cost) {
		if (!canAffordEnergyCost(suit, cost))
			return false;
		pillar[suit].tap(cost);
		return true;
	}
}

// block
// life <=0 end game
// change board color to indicate turn
// can't attack on first turn creatures
// spells and deck element
// highlight both creature cards when mouse over
// draw cost via higlighting pillars
// hide opponents hand
// all select white frame rather than shifting
// cost on opponent creature mouseover as well
// better paint of life & cost