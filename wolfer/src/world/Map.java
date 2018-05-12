package world;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import message.Controls;
import message.IdentifyPlayer;
import message.MapLayout;
import message.Message;
import message.MoveCharTo;
import draw.Painter;

public class Map {

	int width, height;
	int wolfPlayer; // server var
	Character wolf;
	Character hunter;
	boolean isWolf; // client var

	int map[][];
	int[][] corners;

	Map() {
		width = 1;
		height = 1;
		map = new int[width][height];
		wolf = new Character(0, 0, 0);
		hunter = new Character(0, 0, 0);
	}

	void init() {
		wolfPlayer = Util.randint(0, 2);
		width = 50;
		height = 50;
		wolf = new Character(Util.randint(0, width), Util.randint(0, height),
				.15f);
		hunter = new Character(Util.randint(0, width), Util.randint(0, height),
				.05f);
		map = new int[width][height];
		for (int i = 0; i < width; i++) {
			map[i][0] = 1;
			map[i][height - 1] = 1;
		}
		for (int i = 0; i < height; i++) {
			map[0][i] = 1;
			map[width - 1][i] = 1;
		}
		for (int i = 10; i < 20; i++) {
			map[10][i] = 1;
		}
	}

	void init(BufferedImage image) {
		// map
		width = image.getWidth();
		height = image.getHeight();
		map = new int[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pix = image.getRGB(x, y);
				pix = pix != -1 ? 1 : 0;
				int ty = height - 1 - y;
				map[x][ty] = pix;
			}
		}

		// char
		wolfPlayer = Util.randint(0, 2);
		wolf = new Character(Util.randint(0, width), Util.randint(0, height),
				.15f);
		hunter = new Character(Util.randint(0, width), Util.randint(0, height),
				.05f);
	}

	int isMap1(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		else
			return map[x][y] == 1 ? 1 : 0;
	}

	void computeCorners() {
		ArrayList<int[]> corners = new ArrayList<int[]>();
		for (int x = 0; x < width + 1; x++)
			for (int y = 0; y < height + 1; y++) {
				int tl = isMap1(x - 1, y - 1);
				int tr = isMap1(x, y - 1);
				int bl = isMap1(x - 1, y);
				int br = isMap1(x, y);
				int sum = tl + tr + bl + br;
				switch (sum) {
				case 0:
				case 4:
					break;
				case 1:
					corners.add(new int[] { x, y, 1 });
					break;
				case 3:
					corners.add(new int[] { x, y, 3 });
					break;
				case 2:
					if (tl + br == 2 || tr + bl == 2)
						corners.add(new int[] { x, y, 2 });
					break;
				}
			}
		this.corners = corners.toArray(new int[corners.size()][]);
	}

	// old
	void paintLight(Graphics2D brush, float x, float y) {
		if (corners == null)
			return;

		int n = corners.length;
		brush.setColor(new Color(1f, 1f, .5f, .5f));
		float[] angles = new float[n];
		int[] sorted = new int[n]; // sort order
		float[] distSq = new float[n];

		for (int i = 0; i < n; i++) {
			angles[i] = Util.angle(x, y, corners[i][0], corners[i][1]);
			sorted[i] = i;
			distSq[i] = Util.magnitudeSq(corners[i][0] - x, corners[i][1] - y);
		}

		// sort
		for (int i = 1; i < n; i++)
			for (int j = i; j > 0 && angles[sorted[j]] < angles[sorted[j - 1]]; j--) {
				int t = sorted[j];
				sorted[j] = sorted[j - 1];
				sorted[j - 1] = t;
			}

		// paint rays
		// int s = Painter.SCALE;
		// for (int i = 0; i < n; i++) {
		// float dx = corners[i][0] - x;
		// float dy = corners[i][1] - y;
		// float[] intersect = Util.rayTraceNudge(map, x, y, dx, dy);
		//
		// brush.setColor(Color.LIGHT_GRAY);
		//
		// if (intersect[4] < distSq[i])
		// continue;
		// // brush.setColor(Color.RED);
		//
		// brush.drawLine((int) (x * s), (int) (y * s),
		// (int) (intersect[0] * s), (int) (intersect[1] * s));
		// int c = 4;
		// brush.fillArc((int) (intersect[0] * s - c / 2), (int) (intersect[1]
		// * s - c / 2), c, c, 0, 360);
		//
		// brush.drawLine((int) (x * s), (int) (y * s),
		// (int) (intersect[2] * s), (int) (intersect[3] * s));
		// brush.fillArc((int) (intersect[2] * s - c / 2), (int) (intersect[3]
		// * s - c / 2), c, c, 0, 360);
		// }

		// if (1 == 1)
		// return;

		// paint triangles
		int s = Painter.SCALE;
		brush.setColor(new Color(.5f, .5f, 0, .5f));
		// brush.setColor(new Color(120, 120, 0));
		int[] xPoints = new int[n * 2];
		int[] yPoints = new int[n * 2];
		int nVis = 0;
		for (int i = 0; i < n; i++) {
			int j = sorted[i];
			float dx = corners[j][0] - x;
			float dy = corners[j][1] - y;
			float[] intersect = Util.rayTraceNudge(map, x, y, dx, dy);
			if (intersect[4] < distSq[j])
				continue;
			xPoints[nVis] = (int) (intersect[0] * s);
			yPoints[nVis++] = (int) (intersect[1] * s);
			xPoints[nVis] = (int) (intersect[2] * s);
			yPoints[nVis++] = (int) (intersect[3] * s);
		}
		if (nVis > 0)
			brush.fillPolygon(xPoints, yPoints, nVis);
	}

	Polygon paintLight(float x, float y, float startAngle, float widthAngle) {
		if (corners == null)
			return null;

		int n = corners.length;
		float[] angles = new float[n];
		int[] sorted = new int[n]; // sort order
		float[] distSq = new float[n];

		for (int i = 0; i < n; i++) {
			angles[i] = Util.angle(x, y, corners[i][0], corners[i][1]);
			angles[i] = Util.angle0to2PI(angles[i] - startAngle);
			sorted[i] = i;
			distSq[i] = Util.magnitudeSq(corners[i][0] - x, corners[i][1] - y);
		}

		// sort
		for (int i = 1; i < n; i++)
			for (int j = i; j > 0 && angles[sorted[j]] < angles[sorted[j - 1]]; j--) {
				int t = sorted[j];
				sorted[j] = sorted[j - 1];
				sorted[j - 1] = t;
			}

		// paint triangles
		int s = Painter.SCALE;
		// brush.setColor(new Color(120, 120, 0));

		int[] xPoints = new int[n * 2];
		int[] yPoints = new int[n * 2];
		float[] intersect = Util.rayTrace(map, x, y,
				(float) Math.cos(startAngle + widthAngle),
				(float) Math.sin(startAngle + widthAngle));
		xPoints[0] = (int) (intersect[0] * s);
		yPoints[0] = (int) (intersect[1] * s);
		xPoints[1] = (int) (x * s);
		yPoints[1] = (int) (y * s);
		intersect = Util.rayTrace(map, x, y, (float) Math.cos(startAngle),
				(float) Math.sin(startAngle));
		xPoints[2] = (int) (intersect[0] * s);
		yPoints[2] = (int) (intersect[1] * s);
		int nVis = 3;

		for (int i = 0; i < n; i++) {
			int j = sorted[i];
			if (angles[j] > widthAngle)
				break;
			float dx = corners[j][0] - x;
			float dy = corners[j][1] - y;
			intersect = Util.rayTraceNudge(map, x, y, dx, dy);
			if (intersect[4] < distSq[j])
				continue;
			xPoints[nVis] = (int) (intersect[0] * s);
			yPoints[nVis++] = (int) (intersect[1] * s);
			xPoints[nVis] = (int) (intersect[2] * s);
			yPoints[nVis++] = (int) (intersect[3] * s);
		}
		return new Polygon(xPoints, yPoints, nVis);
	}

	Message toInitMessage() {
		Message head = new MapLayout(width, height, map);
		head.nextMessage = new MoveCharTo(true, wolf.x, wolf.y, 1, 0);
		head.nextMessage.nextMessage = new MoveCharTo(false, hunter.x,
				hunter.y, 1, 0);
		return head;
	}

	Message toUpdateMessage() {
		Message m = new MoveCharTo(true, wolf.x, wolf.y, wolf.dirx, wolf.diry);
		m.nextMessage = new MoveCharTo(false, hunter.x, hunter.y, hunter.dirx,
				hunter.diry);
		return m;
	}

	void update() {
		moveChar(wolf);
		moveChar(hunter);
	}

	void moveChar(Character c) {
		if (c.controls == null)
			return;

		int newcoord;
		c.speed = .8f;
		if (Control.isPressed(c.controls.down))
			if (map[(int) c.x][newcoord = (int) (c.y + c.speed + c.hsize)] == 1)
				c.y = newcoord - c.hsize - Util.EPSILON;
			else
				c.y += c.speed;
		if (Control.isPressed(c.controls.up))
			if (map[(int) c.x][newcoord = (int) (c.y - c.speed - c.hsize)] == 1)
				c.y = newcoord + c.hsize + 1;
			else
				c.y -= c.speed;
		if (Control.isPressed(c.controls.left))
			if (map[newcoord = (int) (c.x - c.speed - c.hsize)][(int) c.y] == 1)
				c.x = newcoord + c.hsize + 1;
			else
				c.x -= c.speed;
		if (Control.isPressed(c.controls.right))
			if (map[newcoord = (int) (c.x + c.speed + c.hsize)][(int) c.y] == 1)
				c.x = newcoord - c.hsize - Util.EPSILON;
			else
				c.x += c.speed;
		c.dirx = c.controls.mousex - c.x * Painter.SCALE;
		c.diry = c.controls.mousey - c.y * Painter.SCALE;
		float mag = Util.magnitude(c.dirx, c.diry);
		c.dirx /= mag;
		c.diry /= mag;
	}

	void handleInput(int character, Controls controls) {
		Character c = character == wolfPlayer ? wolf : hunter;
		c.controls = controls;
	}

	public void updateFromServer(Message m) {
		while (m != null) {
			switch (m.getCode()) {
			case MapLayout.CODE:
				updateFromServer((MapLayout) m);
				break;
			case MoveCharTo.CODE:
				updateFromServer((MoveCharTo) m);
				break;
			case IdentifyPlayer.CODE:
				updateFromServer((IdentifyPlayer) m);
				break;
			}
			m = m.nextMessage;
		}
	}

	void updateFromServer(MapLayout m) {
		width = m.width;
		height = m.height;
		map = m.map;
		computeCorners();
	}

	void updateFromServer(MoveCharTo m) {
		Character c = m.wolf ? wolf : hunter;
		c.x = m.x;
		c.y = m.y;
		c.dirx = m.dirx;
		c.diry = m.diry;
	}

	void updateFromServer(IdentifyPlayer m) {
		isWolf = m.wolf;
	}

	void paint(Graphics2D brush) {
		if (isWolf)
			paintWolfView(brush);
		else
			paintHunterView(brush);
	}

	
	void paintWolfView(Graphics2D brush) {
		int bsize = Painter.SCALE;

		// range
		brush.setClip(new Rectangle2D.Float(0, 0, width * bsize, height * bsize));
		brush.setColor(Color.DARK_GRAY);
		brush.fillRect(0, 0, width * bsize, height * bsize);
		float viewDist = 200;
		brush.setClip(new Ellipse2D.Float(hunter.x * bsize - viewDist, hunter.y
				* bsize - viewDist, viewDist * 2, viewDist * 2));

		// draw hunter
		brush.setColor(Color.blue);
		int hsize = (int) (bsize * hunter.hsize);
		int[][] rr = Util.rotatedRect(hunter.x * bsize, hunter.y * bsize,
				hsize, hsize, hunter.dirx, hunter.diry);
		brush.fillPolygon(rr[0], rr[1], 4);

		// lighting
		float angle = Util.angle(0, 0, hunter.dirx, hunter.diry);
		float angleDif = (float) (30f / 180 * Math.PI);
		float angleStart = angle - angleDif;
		if (angleStart <= -Math.PI)
			angleStart += 2 * Math.PI;
		Polygon light = paintLight(hunter.x, hunter.y, angleStart, angleDif * 2);

		if (light != null) {
			GradientPaint redtowhite = new GradientPaint(hunter.x * bsize,
					hunter.y * bsize, Color.WHITE, hunter.x * bsize
							+ hunter.dirx * viewDist, hunter.y * bsize
							+ hunter.diry * viewDist, Color.DARK_GRAY);
			brush.setPaint(redtowhite);
			brush.fillPolygon(light);
			brush.clip(light);
		}

		// draw map
		// brush.setColor(Color.WHITE);
		// for (int y = 0; y < height; y++)
		// for (int x = 0; x < width; x++) {
		// if (map[x][y] == 1)
		// brush.fillRect(x * bsize, y * bsize, bsize, bsize);
		// }

		// draw wolf
		brush.setColor(Color.red);
		hsize = (int) (bsize * wolf.hsize);
		rr = Util.rotatedRect(wolf.x * bsize, wolf.y * bsize, hsize, hsize,
				wolf.dirx, wolf.diry);
		brush.fillPolygon(rr[0], rr[1], 4);
	}

	void paintHunterView(Graphics2D brush) {
		int bsize = Painter.SCALE;

		// range
		brush.setClip(new Rectangle2D.Float(0, 0, width * bsize, height * bsize));
		brush.setColor(Color.DARK_GRAY);
		brush.fillRect(0, 0, width * bsize, height * bsize);
		float viewDist = 200;
		brush.setClip(new Ellipse2D.Float(hunter.x * bsize - viewDist, hunter.y
				* bsize - viewDist, viewDist * 2, viewDist * 2));

		// draw hunter
		brush.setColor(Color.blue);
		int hsize = (int) (bsize * hunter.hsize);
		int[][] rr = Util.rotatedRect(hunter.x * bsize, hunter.y * bsize,
				hsize, hsize, hunter.dirx, hunter.diry);
		brush.fillPolygon(rr[0], rr[1], 4);

		// lighting
		float angle = Util.angle(0, 0, hunter.dirx, hunter.diry);
		float angleDif = (float) (30f / 180 * Math.PI);
		float angleStart = angle - angleDif;
		if (angleStart <= -Math.PI)
			angleStart += 2 * Math.PI;
		Polygon light = paintLight(hunter.x, hunter.y, angleStart, angleDif * 2);

		if (light != null) {
			GradientPaint redtowhite = new GradientPaint(hunter.x * bsize,
					hunter.y * bsize, Color.WHITE, hunter.x * bsize
							+ hunter.dirx * viewDist, hunter.y * bsize
							+ hunter.diry * viewDist, Color.DARK_GRAY);
			brush.setPaint(redtowhite);
			brush.fillPolygon(light);
			brush.clip(light);
		}

		// draw map
		// brush.setColor(Color.WHITE);
		// for (int y = 0; y < height; y++)
		// for (int x = 0; x < width; x++) {
		// if (map[x][y] == 1)
		// brush.fillRect(x * bsize, y * bsize, bsize, bsize);
		// }

		// draw wolf
		brush.setColor(Color.red);
		hsize = (int) (bsize * wolf.hsize);
		rr = Util.rotatedRect(wolf.x * bsize, wolf.y * bsize, hsize, hsize,
				wolf.dirx, wolf.diry);
		brush.fillPolygon(rr[0], rr[1], 4);
	}
}
