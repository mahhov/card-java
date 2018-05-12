import java.awt.*;

public class Map {
	private static final int SCREEN_SIZE = 800;
	private Color[] team;
	private final int mapSize;
	private Point[][][] point;
	private int field;
	private Drawer drawer;
	private Input inputHandler;

	Map(int mapSize, Color[] colors, Graphics g) {
		this.mapSize = mapSize;
		drawer = new Drawer(g);
		team = colors;
		inputHandler = new Input();
		point = new Point[2][mapSize][mapSize];
		for (int v = 0; v < mapSize; v++)
			for (int vv = 0; vv < mapSize; vv++) {
				point[0][v][vv] = new normalPoint();
				point[1][v][vv] = new normalPoint();
			}
		initializePoint(randomPoint(), randomPoint(), 50, 1);
		initializePoint(randomPoint(), randomPoint(), 50, 2);
		initializePoint(randomPoint(), randomPoint(), 50, 3);
		field = 0;
	}

	private int randomPoint() {
		return (int) (Math.random() * mapSize);
	}

	private void initializePoint(int x, int y, int power, int owner) {
		point[0][y][x] = new normalPoint(power, owner);
		point[1][y][x] = new normalPoint(power, owner);
	}

	private void initializeRain(int x, int y, int power, int owner) {
		point[0][y][x].createRain(power, owner);
		point[1][y][x].createRain(power, owner);
	}

	public void run(int[] input) {
		// input
		inputHandler.handleInput(input[0], input[1], input[2], input[3]);

		// update points
		for (int y = 0; y < mapSize; y++)
			for (int x = 0; x < mapSize; x++)
				point[field][y][x].update(getAdjacents(1 - field, x, y));

		// draw points
		drawer.draw();

		// switch field
		field = 1 - field;
	}

	private int[] getAdjacents(int f, int cx, int cy) {
		int[] result = new int[team.length];
		// if (point[f][cy][cx].owner != 0)
		for (int x = cx - 1; x <= cx + 1; x++)
			for (int y = cy - 1; y <= cy + 1; y++)
				if (!(x == cx & y == cy) & isValid(x, y))
					result[point[f][y][x].owner] += point[f][y][x].power / 5;
		result[0] += 5;
		return result;
	}

	private boolean isValid(int x, int y) {
		if (x < 0 | y < 0 | x >= mapSize | y >= mapSize)
			return false;
		else
			return true;
	}

	private class Drawer {
		private double centerX, centerY;
		private int zoom; // 150 to 25
		private double scrollAmount;// how much to move left,right,up,down
		private Graphics brush;

		public Drawer(Graphics g) {
			brush = g;
			centerX = (int) mapSize / 2;
			centerY = (int) mapSize / 2;
			zoom = 50;
			scrollAmount = .25;
		}

		public void draw() {
			int[] bounds = getBounds(); // {left, top, right, bottom,x,y,size}
			Color[] c;
			int rain;
			for (int x = bounds[0]; x <= bounds[2]; x++)
				for (int y = bounds[1]; y <= bounds[3]; y++) {
					c = point[field][y][x].getColor();
					brush.setColor(c[1]);
					brush.fillRect(bounds[4] + x * bounds[6], bounds[5] + y
							* bounds[6], bounds[6], bounds[6]);
					rain = (int) (bounds[6] * point[field][y][x].getRain());
					if (rain > 0) {
						brush.setColor(c[2]);
						brush.fillOval(bounds[4] + x * bounds[6] + bounds[6]
								/ 2 - rain / 2, bounds[5] + y * bounds[6]
								+ bounds[6] / 2 - rain / 2, rain, rain);
					}
					if (false) {
						rain = (int) (bounds[6] * 0.5);
						brush.setColor(Color.black);
						brush.drawOval(bounds[4] + x * bounds[6] + bounds[6]
								/ 2 - rain / 2, bounds[5] + y * bounds[6]
								+ bounds[6] / 2 - rain / 2, rain, rain);
					}
					brush.setColor(c[0]);
					brush.drawRect(bounds[4] + x * bounds[6] + 1, bounds[5] + y
							* bounds[6] + 1, bounds[6] - 2, bounds[6] - 2);
//					brush.drawString((int) point[field][y][x].power + "",
//							bounds[4] + x * bounds[6] + 10, bounds[5] + y
//									* bounds[6] + 10);
				}
		}

		/**
		 * gets which points are in view and should be redrawn
		 * 
		 * @return int[] {left, top, right, bottom,x,y,width,height}
		 */
		private int[] getBounds() {
			int[] result = new int[7];// {left, top, right, bottom,x,y,size}
			result[6] = zoom;// size
			int side = (int) (SCREEN_SIZE / 2 / zoom) + 1;
			result[0] = inBounds(centerX - side); // left
			result[1] = inBounds(centerY - side); // top
			result[2] = inBounds(centerX + side);// right
			result[3] = inBounds(centerY + side);// bottom
			result[4] = (int) (-zoom * centerX + SCREEN_SIZE / 2 - zoom / 2);// x
			result[5] = (int) (-zoom * centerY + SCREEN_SIZE / 2 - zoom / 2);// y
			return result;
		}

		private int inBounds(double n) {
			if (n < 0)
				return 0;
			if (n > mapSize - 1)
				return mapSize - 1;
			return (int) n;
		}

		private double inBoundsD(double n) {
			if (n < 0)
				return 0;
			if (n > mapSize - 1)
				return mapSize - 1;
			return n;
		}

		private void zoom(int amount) {
			zoom += amount * -10;
			if (zoom > 130)
				zoom = 130;
			if (zoom < 20)
				zoom = 20;
		}

		public void scroll(boolean[] directions) {
			double t = scrollAmount / zoom * 150;
			if (directions[0])// left
				centerX = inBoundsD(centerX - t);
			if (directions[1])// up
				centerY = inBoundsD(centerY - t);
			if (directions[2])// right
				centerX = inBoundsD(centerX + t);
			if (directions[3])// down
				centerY = inBoundsD(centerY + t);
		}
	}

	private abstract class Point {
		protected double power;
		protected int owner;
		protected Rain rain;

		private class Rain {
			private int power;
			private int time;
			private int owner;

			Rain(int power, int owner) {
				if (power > 1000)
					power = 1000;
				this.power = power;
				this.time = 8;
				this.owner = owner;
			}
		}

		public Point() {
			power = 4;
			owner = 0;
			rain = new Rain(0, 0);
		}

		public Point(int power, int owner) {
			this.power = power;
			this.owner = owner;
			rain = new Rain(0, 0);
		}

		public boolean createRain(int power, int owner) {
			if (rain.power == 0) {
				rain = new Rain(power, owner);
				return true;
			}
			return false;
		}

		private Color[] getColor() {
			Color c = team[owner];
			return new Color[] { c, modifyColor(c, (int) power),
					modifyColor(team[rain.owner], rain.power / 10) };
		}

		private Color modifyColor(Color c, int amount) {
			int r = c.getRed() - 2 * amount;
			if (r < 0)
				r = 0;
			int g = c.getGreen() - 2 * amount;
			if (g < 0)
				g = 0;
			int b = c.getBlue() - 2 * amount;
			if (b < 0)
				b = 0;
			return new Color(r, g, b);
		}

		private double getRain() {
			if (rain.power == 0)
				return -1;
			return (rain.time + 3) / 25.0;
		}

		public void update(int[] change) {
			// charge
			int majority = maxValueIndex(change);
			sort(change);
			int value = change[change.length - 1] - change[change.length - 2];
			add(value, majority);

			// rain
			rain.time--;
			if (rain.time < 0) {
				add(rain.power, rain.owner);
				rain = new Rain(0, 0);
			}
		}

		private void add(int p, int own) {
			double pow = p / 10.0;
			if (own == owner) {
				power += pow;
			} else {
				power = power - pow;
				if (power == 0)
					owner = 0;
				else if (power < 0) {
					power *= -1;
					owner = own;
				}
			}
			if (power > 100)
				power = 100;
			if (power > 10 & owner == 0)
				power = 10;
		}
	}

	private class normalPoint extends Point {

		public normalPoint() {
			super();
		}

		public normalPoint(int power, int owner) {
			super(power, owner);
		}
	}

	private void sort(int[] a) {
		int loc, t;
		for (int i = 1; i < a.length; i++) {
			loc = 0;
			t = a[i];
			while (loc < i && a[loc] <= t)
				loc++;
			for (int j = i; j > loc; j--)
				a[j] = a[j - 1];
			a[loc] = t;
		}
	}

	private int maxValueIndex(int[] a) {
		int max = 0;
		for (int i = 1; i < a.length; i++)
			if (a[i] > a[max])
				max = i;
		return max;
	}

	private class Input {
		int currentX, currentY;
		int downX, downY;
		int mouse;// 0=up, 1=dowwn
		int scrollSensitivity;// how close need to be to border to scroll
		int[][] tempRain;

		private Input() {
			currentX = 0;
			currentY = 0;
			downX = 0;
			downY = 0;
			mouse = 0;
			scrollSensitivity = 100;
		}

		private void handleInput(int mouse, int x, int y, int scroll) {
			// mouse: 0=no change, 1=just down, 2-just up

			// zoom
			drawer.zoom(scroll);

			// scroll
			boolean[] directions = new boolean[4];// left,up,right,down
			if (x < scrollSensitivity)
				directions[0] = true;
			if (y < scrollSensitivity)
				directions[1] = true;
			if (x > SCREEN_SIZE - scrollSensitivity)
				directions[2] = true;
			if (y > SCREEN_SIZE - scrollSensitivity)
				directions[3] = true;
			drawer.scroll(directions);

			// convert X,Y to mapPoints
			x = drawer.inBounds(drawer.centerX + (x - (SCREEN_SIZE / 2.0))
					/ drawer.zoom + .5);
			y = drawer.inBounds(drawer.centerY + (y - (SCREEN_SIZE / 2.0))
					/ drawer.zoom + .5);

			currentX = x;
			currentY = y;

			// update mouse state
			int x1 = Math.min(currentX, downX);
			int x2 = Math.max(currentX, downX);
			int y1 = Math.min(currentY, downY);
			int y2 = Math.max(currentY, downY);

			if (mouse == 1) {
				this.mouse = 1;
				this.downX = x;
				this.downY = y;
			}
			if (mouse == 2) {
				this.mouse = 0;
				for (int rainX = x1; rainX <= x2; rainX++)
					for (int rainY = y1; rainY <= y2; rainY++)
						initializeRain(rainX, rainY, 1000, 0);
			}
			if (this.mouse == 1) {
				tempRain = new int[(y2 - y1 + 1) * (x2 - x1 + 1)][];
				int i = 0;
				for (int rainX = x1; rainX <= x2; rainX++)
					for (int rainY = y1; rainY <= y2; rainY++) {
						tempRain[i] = new int[] { rainX, rainY };
						i++;
					}
			}
		}
	}

}
