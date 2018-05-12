package raytrace;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

	private Vector[] v;
	private Vector[] vnorm;
	private Surface[] surface;
	public Polyhedron poly; // want a cracker

	private float xmin = Float.MAX_VALUE, xmax = Float.MIN_VALUE,
			ymin = Float.MAX_VALUE, ymax = Float.MIN_VALUE,
			zmin = Float.MAX_VALUE, zmax = Float.MIN_VALUE;

	private boolean flipSurface;

	private int vn, surfacen, vnormn;

	Parser(String filename, Vector ka, Vector kd, Vector ks, float sp,
			float ref, boolean flipSurface) {
		this.flipSurface = flipSurface;

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				count(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setupVars();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				readLine(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		poly = new Polyhedron(ka, kd, ks, sp, ref, surface);
		poly.setBounds(xmin, xmax, ymin, ymax, zmin, zmax);
		System.out.println("done parsing");
	}

	private void setupVars() {
		// poly= new Polyhedron(ka, kd, ks, sp, n);
		v = new Vector[vn];
		vnorm = new Vector[vnormn];
		if (flipSurface)
			surfacen *= 2;
		surface = new Surface[surfacen];
		vn = 0;
		vnormn = 0;
		surfacen = 0;
	}

	private void count(String line) {
		String[] p = line.split(" ", 2);
		switch (p[0]) {
		case "v":
			vn++;
			break;
		case "vn":
			vnormn++;
			break;
		case "f":
			surfacen++;
			break;
		}
	}

	private void readLine(String line) {
		String[] p = line.split(" ");
		switch (p[0]) {
		case "v":
			v[vn++] = getVector(p);
			break;
		case "vn":
			vnorm[vnormn++] = getVector(p);
			break;
		case "f":
			surface[surfacen++] = getSurface(p);
			if (flipSurface) {
				surface[surfacen] = getSurface(p);
				surface[surfacen++].flipNormal();
			}
			break;
		}
	}

	private Vector getVector(String[] s) {
		float f[] = new float[3];
		int n = 0;
		float pf;
		for (int i = 0; i < s.length && n < 3; i++) {
			try {
				pf = Float.parseFloat(s[i]);
				f[n++] = pf;
			} catch (NumberFormatException e) {
			}
		}

		if (f[0] < xmin)
			xmin = f[0];
		if (f[0] > xmax)
			xmax = f[0];
		if (f[1] < ymin)
			ymin = f[1];
		if (f[1] > ymax)
			ymax = f[1];
		if (f[2] < zmin)
			zmin = f[2];
		if (f[2] > zmax)
			zmax = f[2];

		return new Vector(f[0], f[1], f[2]);
	}

	private Surface getSurface(String[] s) {
		int pi;

		int n = 0;
		for (int i = 0; i < s.length; i++) {
			try {
				pi = Integer.parseInt(s[i].split("/")[0]);
				n++;
			} catch (NumberFormatException e) {
			}
		}

		Vector[] vi = new Vector[n];
		n = 0;
		for (int i = 0; i < s.length; i++) {
			try {
				pi = Integer.parseInt(s[i].split("/")[0]);
				vi[n++] = v[pi - 1];
			} catch (NumberFormatException e) {
			}
		}

		return new Surface(vi);
	}

}
