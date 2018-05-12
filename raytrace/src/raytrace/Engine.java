package raytrace;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Engine {

	Camera c;
	Scene s;

	public static void main(String[] args) {
		if (args.length == 0)
			new Engine(new InputData("input_04"));
		else {
			InputData input = new InputData(args[0]);
			new Engine(input);
		}
	}

	private Engine(InputData in) {
		s = new Scene(in.reflection, in.pn, in.dn);

		Parser parser = new Parser(in.obj, in.ka, in.kd, in.ks, in.sp, in.ref,
				in.flipSurface);
		s.add(parser.poly);

		float[][] m = new float[3][];
		m[0] = new float[] { 1, 0, 0 };
		m[1] = new float[] { 0, 2, 0 };
		m[2] = new float[] { 0, 0, 1f };
		parser.poly.setTransformation(new MatrixTransformation(m));

		for (int k = 0; k < in.dn; k++)
			s.add(in.dirLight[k], true);
		for (int k = 0; k < in.pn; k++)
			s.add(in.pointLight[k], false);

		c = new Camera(in.cameraX, in.cameraY, in.cameraZ, in.cameraDirX,
				in.cameraDirY, in.cameraDirZ, in.yup, in.cameraHoriz,
				in.cameraVert);

		long startTime = System.currentTimeMillis();
		BufferedImage i = s.drawThread(in.resX, in.resY, c);
		long endTime = System.currentTimeMillis();
		System.out.println("elapsed: " + (endTime - startTime));
		try {
			ImageIO.write(i, "png", new File(in.out + ".png"));
		} catch (IOException e) {
		}
		System.out.println("done writing");
	}

	private Engine() {
		s = new Scene(4);

		Sphere sphere1 = new Sphere(new Vector(0, 0, 0), 10, new Vector(.1f,
				.6f, .0f), new Vector(1f, 1f, 0f), new Vector(.8f, .8f, .8f),
				16, 0.5f); // yellow
		Sphere sphere2 = new Sphere(new Vector(25, 50, 0), 10, new Vector(.1f,
				.1f, .1f), new Vector(0f, 0f, .7f), new Vector(0, 0, 1), 25,
				0.5f); // blue
		Sphere sphere3 = new Sphere(new Vector(-15, 0, 0), 5, new Vector(.3f,
				.1f, .0f), new Vector(1f, 0f, 0f), new Vector(.8f, .8f, .8f),
				160, .5f); // red
		Sphere sphere4 = new Sphere(new Vector(-15, 0, 10), 5, new Vector(.3f,
				.1f, .0f), new Vector(1f, 0f, 0f), new Vector(.8f, .8f, .8f),
				160, .5f); // red
		Cube cube = new Cube(new Vector(-15, -15, -15f), new Vector(30, 0, 0),
				new Vector(0, 30, 0), new Vector(0, 0, 10), new Vector(.0f,
						.1f, .0f), new Vector(0f, .5f, .5f), new Vector(1f, 0,
						0), 16, .5f);
		s.add(sphere2);
		s.add(sphere1);
		s.add(sphere3);
		s.add(sphere4);
		s.add(cube);

		// uncoment for matrix transformations:
		float[][] m = new float[3][];
		m[0] = new float[] { 2, 0, 0 };
		m[1] = new float[] { 0, 2, 0 };
		m[2] = new float[] { 0, 0, 2.5f };
		cube.setTransformation(new MatrixTransformation(m));

		s.add(new Light(new Vector(-50, 30, -30), new Vector(.6f, .6f, .6f)),
				true);
		c = new Camera(0, -50, 0, 0, 1, 0, 1.23f, 1.23f);

		long startTime = System.currentTimeMillis();
		BufferedImage i = s.drawThread(1500, 1500, c);
		long endTime = System.currentTimeMillis();
		System.out.println("elapsed: " + (endTime - startTime));
		try {
			ImageIO.write(i, "png", new File("a_00.png"));
		} catch (IOException e) {
		}
		System.out.println("done writing");
	}

	private static class InputData {
		int reflection;

		String obj;
		Vector ka, kd, ks;
		float sp;
		float ref;
		boolean flipSurface;

		float cameraX, cameraY, cameraZ;
		float cameraDirX, cameraDirY, cameraDirZ;
		boolean yup;
		float cameraHoriz, cameraVert;

		int pn, dn;
		Light[] pointLight, dirLight;

		String out;
		int resX, resY;

		InputData(String input) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(input));
				String line;
				while ((line = br.readLine()) != null) {
					consider(line);
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		private void consider(String line) {
			String[] p = line.split(" ");
			switch (p[0]) {
			case "reflection":
				this.reflection = readi(p[1], line);
				break;
			case "object":
				this.obj = p[1];
				break;
			case "ka":
				this.ka = readv(p, line, 1);
				break;
			case "kd":
				this.kd = readv(p, line, 1);
				break;
			case "ks":
				this.ks = readv(p, line, 1);
				break;
			case "sp":
				this.sp = readf(p[1], line);
				break;
			case "ref":
				this.ref = readf(p[1], line);
				break;
			case "flip":
				this.flipSurface = (readf(p[1], line) == 1);
				break;
			case "camera":
				// camera x y z dirx diry dirz upaxis(z or y)
				// viewingAngleHorizontal viewingAngleVertical
				this.cameraX = readf(p[1], line);
				this.cameraY = readf(p[2], line);
				this.cameraZ = readf(p[3], line);
				this.cameraDirX = readf(p[4], line);
				this.cameraDirY = readf(p[5], line);
				this.cameraDirZ = readf(p[6], line);
				this.yup = (p[7].equals("y"));
				this.cameraHoriz = readf(p[8], line);
				this.cameraVert = readf(p[9], line);
				break;
			case "light":
				this.pointLight = new Light[readi(p[1], line)];
				this.dirLight = new Light[readi(p[2], line)];
				break;
			case "pointlight":
				this.pointLight[pn++] = new Light(readv(p, line, 1), readv(p,
						line, 4));
				break;
			case "dirlight":
				this.dirLight[dn++] = new Light(readv(p, line, 1), readv(p,
						line, 4));
				break;
			case "out":
				this.out = p[1];
				this.resX = readi(p[2], line);
				this.resY = readi(p[3], line);
				break;
			}
		}

		private float readf(String s, String line) {
			try {
				return Float.parseFloat(s);
			} catch (NumberFormatException e) {
				System.out.println("input file bad format: " + line);
				return 0;
			}
		}

		private int readi(String s, String line) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				System.out.println("input file bad format: " + line);
				return 0;
			}
		}

		private Vector readv(String[] s, String line, int shift) {
			return new Vector(readf(s[shift], line), readf(s[shift + 1], line),
					readf(s[shift + 2], line));
		}

	}

}