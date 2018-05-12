package raytrace;

public class Camera {
	private Vector loc, dir, up, right;

	private float nhoriz, nvert;

	// image screen is 2ux2u square 1u away and perpendicular from camera

	public Camera(float x, float y, float z, float dx, float dy, float dz,
			float horiz, float vert) {
		loc = new Vector(x, y, z);
		setDir(dx, dy, dz);
		setAngle(horiz, vert);
	}

	public Camera(float x, float y, float z, float dx, float dy, float dz,
			boolean yup, float horiz, float vert) {
		loc = new Vector(x, y, z);
		if (yup)
			setDirYup(dx, dy, dz);
		else
			setDir(dx, dy, dz);
		setAngle(horiz, vert);
	}

	private void setDir(float x, float y, float z) {
		if (x == 0 && y == 0)
			y = 0.00001f;
		dir = new Vector(x, y, z, true);
		right = new Vector(dir.y, -dir.x, 0, true);
		up = new Vector(-dir.x * dir.z, -dir.y * dir.z, dir.y * dir.y + dir.x
				* dir.x, true);
	}

	private void setDirYup(float x, float y, float z) {
		if (x == 0 && z == 0)
			z = 0.00001f;
		dir = new Vector(x, y, z, true);
		right = new Vector(-dir.z, 0, dir.x, true);
		up = new Vector(-dir.x * dir.y, dir.z * dir.z + dir.x * dir.x, -dir.y
				* dir.z, true);
	}

	private void setAngle(float horiz, float vert) {
		nhoriz = (float) Math.tan(horiz / 2);
		nvert = (float) Math.tan(vert / 2);
		if (nhoriz < 0)
			nhoriz = -nhoriz;
		if (nvert < 0)
			nvert = -nvert;
	}

	public Ray getRay(float x, float y) { // center screen (0,0)
		// float n = (float) (1 / Math.sqrt(2));
		return new Ray(loc, new Vector(dir, up, -y * nvert, right, x * nhoriz,
				true));
	}

	public String toString() {
		return loc + "\n" + dir + "\n" + dir + "\n" + right;
	}
}
