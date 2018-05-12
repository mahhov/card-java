package raytrace;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Scene {

	Polyhedron[] poly;
	int pn;
	Light[] dirLight, pointLight;
	int dirLightn, pointLightn;

	float scalex;
	float scaley;
	float shiftx;
	float shifty;

	final int reflection;

	Scene(int reflection) {
		poly = new Polyhedron[10];
		dirLight = new Light[10];
		pointLight = new Light[10];
		this.reflection = reflection;
	}

	Scene(int reflection, int pointN, int dirN) {
		poly = new Polyhedron[10];
		dirLight = new Light[dirN];
		pointLight = new Light[pointN];
		this.reflection = reflection;
	}

	public void add(Polyhedron polyhedron) {
		poly[pn++] = polyhedron;
	}

	public void add(Light light, boolean dir) {
		if (!dir)
			pointLight[pointLightn++] = light;
		else
			dirLight[dirLightn++] = light;
	}

	public BufferedImage draw(int width, int height, Camera camera) {
		BufferedImage i = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		// scalex = 2f / width;
		// scaley = 2f / height;
		// shiftx = -1 + scalex / 2;
		// shifty = -1 + scaley / 2;

		// int last = -1;
		// int cur;

		for (int x = 0; x < width; x++) {
			// cur = (10 * x / width);
			// if (cur != last) {
			// last = cur;
			// System.out.println("" + cur);
			// }
			for (int y = 0; y < height; y++) {
				Vector c = getColor(
						camera.getRay(1 * x * scalex + shiftx, 1 * y * scaley
								+ shifty), reflection);
				if (c != null) {
					int color = new Color(c.x < 1 ? c.x : 1, c.y < 1 ? c.y : 1,
							c.z < 1 ? c.z : 1).getRGB();
					i.setRGB(x, y, color);
				}
			}
		}
		System.out.println("done buffering");

		return i;
	}

	public BufferedImage drawThread(int width, int height, Camera camera) {
		BufferedImage i = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		scalex = 2f / width;
		scaley = 2f / height;
		shiftx = -1 + scalex / 2;
		shifty = -1 + scaley / 2;

		int thn = Runtime.getRuntime().availableProcessors();
		// System.out.println("threads: " + thn);
		Thread[] th = new Thread[thn];
		SceneThread[] sth = new SceneThread[thn];
		int ncolumn = width / thn;
		int extra = width - ncolumn * thn;
		ncolumn++;
		int x = 0;

		for (int k = 0; k < thn; k++) {
			if (k == extra)
				ncolumn--;
			sth[k] = new SceneThread(this, x, ncolumn, width, height, camera);
			th[k] = new Thread(sth[k]);
			th[k].start();
			x += ncolumn;
		}

		x = 0;
		ncolumn++;
		for (int k = 0; k < thn; k++) {
			if (k == extra)
				ncolumn--;
			while (th[k].isAlive())
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			for (int xi = 0; xi < ncolumn; xi++)
				for (int y = 0; y < height; y++) {
					i.setRGB(x + xi, y, sth[k].out[xi][y]);
				}
			x += ncolumn;
		}

		// System.out.println("done buffering");

		return i;
	}

	IntersectData getIntersection(Ray ray, float minDistance) {
		IntersectData minIntersect = null;

		for (int i = 0; i < pn; i++) {
			IntersectData intersect = poly[i].intersect(ray, minDistance);
			if (intersect.s != -1) {
				minIntersect = intersect;
				minIntersect.p = i;
				minDistance = intersect.v.t;
			}
		}

		return minIntersect;
	}

	boolean isIntersection(Ray ray, float minDistance) {
		for (int i = 0; i < pn; i++) {
			if (poly[i].isIntersect(ray, minDistance)) {
				return true;
			}
		}

		return false;
	}

	Vector getColor(Ray ray, int reflection) {
		IntersectData minIntersect = getIntersection(ray, -1);

		if (minIntersect == null)
			return null;

		Vector shade = poly[minIntersect.p].getColor(ray.dir, minIntersect.v,
				minIntersect.s, this, dirLight, dirLightn, pointLight,
				pointLightn);

		if (reflection == 0 || poly[minIntersect.p].attrib.ref == 0)
			return shade;

		Ray reflectRay = ray.reflect(minIntersect.v,
				poly[minIntersect.p].getNorm(minIntersect.s, minIntersect.v));
		Vector rshade = getColor(reflectRay, reflection - 1);

		if (rshade == null)
			return shade;

		return Vector.add(shade, 1, rshade, poly[minIntersect.p].attrib.ref);
	}

	static class IntersectData {
		public Vector v;
		public int p; // polyhedron index
		public int s; // surface index

		public IntersectData(Vector v, int s) {
			this.v = v;
			this.s = s;
		}
	}

}
