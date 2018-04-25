package com.company;

class Cpu extends Player {
	
	final int timerMax = 15;
	int timer;
	static final int STAGE_DISCARD = 0, STAGE_PILLAR = 1, STAGE_CREATURE = 2, STAGE_OFFENSE = 3, STAGE_ENDTURN = 4;
	int stage;
	
	Cpu(int life, boolean top) {
		super(life, top);
	}
	
	void beginTurn(int d) {
		super.beginTurn(d);
		stage = 0;
	}
	
	void update(int mouseClick) {
		if (++timer == timerMax) {
			switch (stage++) {
				case STAGE_DISCARD:
					discardAI();
					break;
				case STAGE_PILLAR:
					playPillar();
					break;
				case STAGE_CREATURE:
					playCreature();
					break;
				case STAGE_OFFENSE:
					offenseCreature();
					break;
				case STAGE_ENDTURN:
					requestEndTurn = true;
			}
			timer = 0;
		}
	}
	
	void discardAI() {
		if (selectingDiscard) {
			int suitSum[] = new int[pillar.length];
			for (int i = 0; i < pillar.length; i++)
				suitSum[i] = pillar[i].untapped();
			int discard = 0;
			for (int i = 1; i < hand.size; i++)
				if (!hand.card[i].isPillar() && hand.card[i].number - suitSum[hand.card[i].suit] > hand.card[discard].number - suitSum[hand.card[discard].suit])
					discard = i;
			discardHand(discard);
		}
	}
	
	void playPillar() {
		int maxPillar[] = new int[] {-1, -1, -1, -1};
		int weight[] = new int[] {1, 1, 1, 1};
		Card c;
		for (int i = 0; i < hand.size; i++) {
			c = hand.card[i];
			if (c.isPillar()) {
				if (maxPillar[c.suit] == -1 || hand.card[maxPillar[c.suit]].number < c.number)
					maxPillar[c.suit] = i;
			} else if (c.isCreature())
				weight[c.suit] += c.number;
		}
		int maxScore = 0, maxI = -1, score;
		for (int i = 0; i < pillar.length; i++)
			if (maxPillar[i] != -1) {
				score = weight[i] * hand.card[maxPillar[i]].number;
				if (score > maxScore) {
					maxScore = score;
					maxI = i;
				}
			}
		if (maxI != -1)
			playPillar(maxPillar[maxI]);
	}
	
	int creatureScore(Card card1, Card card2) {
		return card1.number * 3 + card2.number;
	}
	
	void playCreature() {
		int score, maxScore;
		int c1 = 0, c2 = 0;
		for (int suit = 0; suit < pillar.length; suit++) {
			maxScore = 0;
			while (maxScore != -1) {
				maxScore = -1;
				for (int i = 0; i < hand.size; i++)
					if (hand.card[i].suit == suit)
						for (int j = i + 1; j < hand.size; j++)
							if (hand.card[j].suit == suit && canAffordEnergyCost(suit, getEnergyCost(hand.card[i], hand.card[j]))) {
								score = creatureScore(hand.card[i], hand.card[j]);
								if (score > maxScore) {
									maxScore = score;
									c1 = i;
									c2 = j;
								}
								score = creatureScore(hand.card[j], hand.card[i]);
								if (score > maxScore) {
									maxScore = score;
									c1 = j;
									c2 = i;
								}
							}
				if (maxScore != -1)
					playCreature(c1, c2);
			}
		}
	}
	
	void offenseCreature() {
		int score, maxScore, c = 0;
		for (int suit = 0; suit < pillar.length; suit++) {
			maxScore = 0;
			while (maxScore != -1) {
				maxScore = -1;
				for (int i = 0; i < numCreatures; i++)
					if (!creature[i].tapped && creature[i].card[0].suit == suit && canAffordEnergyCost(suit, getEnergyCost(creature[i].card[0], creature[i].card[1]))) {
						score = creatureScore(creature[i].card[0], creature[i].card[1]);
						if (score > maxScore) {
							maxScore = score;
							c = i;
						}
					}
				if (maxScore != -1)
					offenseCreature(c);
			}
		}
	}
	
}
