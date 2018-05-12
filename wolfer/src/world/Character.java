package world;

import message.Controls;

public class Character {
	float x, y;
	float dirx, diry;
	Controls controls;
	float speed;
	float hsize;

	Character(float x, float y, float speed) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		hsize = .5f;
	}

}
