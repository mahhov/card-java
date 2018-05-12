package raytrace;

import java.awt.Color;

public class SceneThread implements Runnable {

	private Scene scene;
	private int column;
	int ncolumn;
	private int height;
	private Camera camera;
	int[][] out;

	public SceneThread(Scene scene, int column, int ncolumn, int width,
			int height, Camera camera) {
		this.scene = scene;
		this.column = column;
		this.ncolumn = ncolumn;
		this.height = height;
		this.camera = camera;
		out = new int[ncolumn][height];
	}

	public void run() {
		int x;
		for (int xi = 0; xi < ncolumn; xi++) {
			x = xi + column;
			for (int y = 0; y < height; y++) {
				Vector c = scene.getColor(
						camera.getRay(1 * x * scene.scalex + scene.shiftx, 1
								* y * scene.scaley + scene.shifty), scene.reflection);
				if (c != null) {
					int color = new Color(c.x < 1 ? c.x : 1, c.y < 1 ? c.y : 1,
							c.z < 1 ? c.z : 1).getRGB();
					out[xi][y] = color;
				}
			}
		}
	}

}
