package world;

public class Util {

	public static final float EPSILON = .0000001f;
	public static final float PI2 = (float) (Math.PI * 2);

	static int randint(int min, int max) {
		return (int) (min + Math.random() * (max - min));
	}

	static void sleep(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static float norm(float dx, float dy) {
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	static int[][] rotatedRect(float x, float y, float halfw, float halfh,
			float dirx, float diry) {
		halfw *= 2;

		float n = norm(dirx, diry);
		dirx /= n;
		diry /= n;

		float frontx = halfh * dirx;
		float fronty = halfh * diry;
		float leftx = halfw * diry;
		float lefty = -halfw * dirx;
		int[] xcoords = new int[] { (int) (x + frontx + leftx),
				(int) (x + frontx - leftx), (int) (x - frontx - leftx),
				(int) (x - frontx + leftx) };
		int[] ycoords = new int[] { (int) (y + fronty + lefty),
				(int) (y + fronty - lefty), (int) (y - fronty - lefty),
				(int) (y - fronty + lefty) };
		return new int[][] { xcoords, ycoords };
	}

	static float angle(float x, float y, float x2, float y2) {
		return (float) Math.atan2(y2 - y, x2 - x);
	}

	static public void sortTest(String[] arg) {
		int[] unsorted = new int[] { 1, 10, 3, 5, 2, 6, 8, 4 };
		int n = unsorted.length;

		int[] order = new int[n]; // sort order

		for (int i = 0; i < n; i++)
			order[i] = i;

		// sort
		for (int i = 1; i < n; i++) {
			for (int j = i; j > 0
					&& unsorted[order[j]] < unsorted[order[j - 1]]; j--) {
				int t = order[j];
				order[j] = order[j - 1];
				order[j - 1] = t;
			}
		}
		// for (int i = 1; i < n; i++) {
		// for (int j = i; j > 0 && unsorted[j] < unsorted[j - 1]; j--) {
		// int t = unsorted[j];
		// unsorted[j] = unsorted[j - 1];
		// unsorted[j - 1] = t;
		// }
		// }

		for (int i = 0; i < n; i++) {
			System.out.println(order[i] + " " + unsorted[order[i]]);
			// System.out.println(unsorted[i]);
		}
	}

	public static float magnitude(float dx, float dy) {
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public static float magnitudeSq(float dx, float dy) {
		return dx * dx + dy * dy;
	}

	public static float[] rayTraceNudge(int[][] map, float x, float y,
			float dx, float dy) {
		float nudge = +.005f; // positive left
		float[] center = rayTrace(map, x, y, dx, dy);
		float[] left = rayTrace(map, x, y, dx + nudge * dy, dy - nudge * dx);
		float[] right = rayTrace(map, x, y, dx - nudge * dy, dy + nudge * dx);
		float dist = max(max(center[2], left[2]), right[2]) + .005f;
		return new float[] { left[0], left[1], right[0], right[1], dist * dist };
	}

	public static float[] rayTrace(int[][] map, float x, float y, float dx,
			float dy) {
		if (dx == 0 && dy == 0)
			dx = 1;
		else {
			float mag = magnitude(dx, dy);
			dx /= mag;
			dy /= mag;
		}

		float traversed = 0;

		while (map[(int) x][(int) y] != 1) {
			float leftDist = x - (int) x;
			float bottomDist = y - (int) y;
			float rightDist = 1 - leftDist;
			float topDist = 1 - bottomDist;
			if (leftDist == 0)
				leftDist = 1;
			if (bottomDist == 0)
				bottomDist = 1;

			float deltaX = 0, deltaY = 0;

			if (dx != 0)
				deltaX = Util.max(rightDist / dx, -leftDist / dx);
			if (dy != 0)
				deltaY = Util.max(topDist / dy, -bottomDist / dy);
			float delta = (deltaX < deltaY && deltaX != 0) || deltaY == 0 ? deltaX
					: deltaY;
			if (delta == 0)
				System.out.println("ni");

			delta *= (1 + EPSILON * 50000); // .005

			x += dx * delta;
			y += dy * delta;

			traversed += delta;
		}

		return new float[] { x, y, traversed };
	}

	public static float max(float a, float b) {
		return a > b ? a : b;
	}

	public static float min(float a, float b) {
		return a < b ? a : b;
	}

	public static float maxabs(float a, float b) {
		if (a < 0)
			a = -a;
		if (b < 0)
			b = -b;
		return a > b ? a : b;
	}

	public static float angle0to2PI(float angle) {
		angle %= Util.PI2;
		if (angle < 0)
			return angle + Util.PI2;
		return angle;
	}

	public static void atan2Test(String[] arg) { // atan2Test
		System.out.println("(1,1): " + Math.atan2(1, 1) / Math.PI * 180); // 45
		System.out.println("(-1, 1): " + Math.atan2(1, -1) / Math.PI * 180); // 135
		System.out.println("(1, -1): " + Math.atan2(-1, 1) / Math.PI * 180); // -45
		System.out.println("(-1, -1): " + Math.atan2(-1, -1) / Math.PI * 180); // -135
		System.out.println("(-1, 0): " + Math.atan2(0, -1) / Math.PI * 180); // 180
	}

	public static void forLoopBreakTest(String[] arg) {
		// prints 0, 1, 2, 3
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			if (i == 3)
				break;
		}
	}

}
