package com.company;

import java.awt.*;

class Card {
	static final int DRAW_MARGIN = 10, DRAW_MARGIN_Y = 20, DRAW_STACK_LARGE = 15, DRAW_STACK_SMALL = 3;
	static final int WIDTH = 35, HEIGHT = 50;
	static final int SUIT_HEART_FIRE = 0, SUIT_SPADE_WATER = 1, SUIT_DIAMOND_EARTH = 2, SUIT_CLUB_AIR = 3;
	static final int TEAM_WHITE = 0, TEAM_BLACK = 1;
	int number;
	int suit;
	String[] cachedText;
	Color cachedColor, cachedHighlightColor, cachedBackColor, cachedBackHighlihghtColor;
	boolean tapped;
	boolean redBorder;
	
	Card(int number, int suit) {
		this.number = number;
		this.suit = suit;
		setCacheText();
		setCacheColor();
	}
	
	boolean isPillar() {
		return number == 3 || number == 6 || number == 9;
	}
	
	boolean isCreature() {
		return !isPillar() && !isCast();
	}
	
	boolean isCast() {
		return number == 1 || number >= 11;
	}
	
	void setCacheText() {
		String[] suitTextMap = new String[] {"\u2661", "\u2664", "\u2662", "\u2667"};
		// suitTextMap = new String[] {"\u2665", "\u2660", "\u2666", "\u2663"};
		String[] numberTextMap = new String[] {"A", "T", "J", "Q", "K"};
		
		String suitText = suitTextMap[suit];
		String numberText;
		if (number == 1)
			numberText = numberTextMap[0];
		else if (number > 9)
			numberText = numberTextMap[number - 9];
		else
			numberText = number + "";
		
		cachedText = new String[] {numberText + " " + suitText, numberText + " ", suitText};
	}
	
	void setCacheColor() {
		int focus = 220, low = 170;
		Color[] color = new Color[] {new Color(focus, low, low), new Color(low / 2, low, focus), new Color(low / 2, focus, low), new Color(focus, focus, low)};
		cachedColor = color[suit];
		
		focus = 250;
		low = 220;
		color = new Color[] {new Color(focus, low, low), new Color(low / 2, low, focus), new Color(low / 2, focus, low), new Color(focus, focus, low)};
		cachedHighlightColor = color[suit];
		
		cachedBackColor = Color.GRAY;
		cachedBackHighlihghtColor = Color.LIGHT_GRAY;
	}
	
	// 0
	static void draw(int x, int y, Color fillColor, Color frameColor, String text, Graphics2D brush) {
		brush.setStroke(new BasicStroke(2));
		brush.setColor(fillColor);
		brush.fillRect(x, y, WIDTH, HEIGHT);
		brush.setColor(frameColor);
		brush.drawRect(x, y, WIDTH, HEIGHT);
		brush.drawString(text, x + 5, y + 15);
	}
	
	// ->0
	static void draw(int slotx, int y, Color fillColor, Color frameColor, Graphics2D brush) {
		draw(getX(slotx, 0, 0), getY(y, 0), fillColor, frameColor, "", brush);
	}
	
	// 1->0
	boolean draw(int x, int y, boolean upsidedown, String text, boolean forceHighlight, Graphics2D brush, Controller.Cord cord) {
		boolean mouseOver = cord != null && (forceHighlight || (cord.x > x && cord.x < x + WIDTH && cord.y > y && cord.y < y + HEIGHT));
		
		Color fillColor;
		if (!upsidedown)
			if (mouseOver)
				fillColor = cachedHighlightColor;
			else
				fillColor = cachedColor;
		else if (mouseOver)
			fillColor = cachedBackHighlihghtColor;
		else
			fillColor = cachedBackColor;
		
		Color frameColor;
		if (redBorder)
			frameColor = Color.RED;
		else if (tapped)
			frameColor = Color.WHITE;
		else
			frameColor = Color.DARK_GRAY;
		
		if (text == null)
			text = cachedText[0];
		
		draw(x, y, fillColor, frameColor, text, brush);
		
		return mouseOver;
	}
	
	// 2->1
	boolean draw(int slotx, int sloty, int slotzLarge, int slotzSmall, int slotw, boolean upsidedown, String text, boolean forceHighlight, Graphics2D brush, Controller.Cord cord) {
		return draw(getX(slotx, slotzLarge, slotzSmall), getY(sloty, slotw), upsidedown, text, forceHighlight, brush, cord);
	}
	
	// ->2
	boolean draw(int slotx, int sloty, int slotzLarge, Graphics2D brush, Controller.Cord cord) {
		return draw(slotx, sloty, slotzLarge, 0, 0, false, null, false, brush, cord);
	}
	
	// ->2
	boolean draw(int slotx, int sloty, int slotzLarge, int slotw, Graphics2D brush, Controller.Cord cord) {
		return draw(slotx, sloty, slotzLarge, 0, slotw, false, null, false, brush, cord);
	}
	
	static int getX(int slotx, int slotzLarge, int slotzSmall) {
		return DRAW_MARGIN + slotx * (WIDTH + DRAW_MARGIN) + slotzLarge * DRAW_STACK_LARGE + slotzSmall * DRAW_STACK_SMALL;
	}
	
	static int getY(int sloty, int slotw) {
		int r = DRAW_MARGIN + sloty * (HEIGHT + DRAW_MARGIN_Y);
		if (slotw != 0)
			if (r * 2 + HEIGHT > Painter.HEIGHT)
				r -= slotw * DRAW_STACK_LARGE;
			else
				r += slotw * DRAW_STACK_LARGE;
		return r;
	}
	
	// unused
	boolean isMouseOver(Controller.Cord cord, int slotx, int sloty, int slotzLarge, int slotzSmall) {
		int x = getX(slotx, slotzLarge, slotzSmall);
		int y = getY(sloty, 0);
		return (cord.x > x && cord.x < x + WIDTH && cord.y > y && cord.y < y + HEIGHT);
	}
	
}
