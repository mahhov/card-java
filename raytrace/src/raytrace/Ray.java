package raytrace;

public class Ray {
	public Vector loc, dir, dirinv;

	public Ray(Vector loc, Vector dir) {
		this.loc = loc;
		this.dir = dir;
		dirinv = new Vector(1 / dir.x, 1 / dir.y, 1 / dir.z);
	}

	public Ray(Ray ray) {
		loc = new Vector(ray.loc.x, ray.loc.y, ray.loc.z);
		dir = new Vector(ray.dir.x, ray.dir.y, ray.dir.z);
	}

	public Vector move(float d) {
		return new Vector(loc.x + dir.x * d, loc.y + dir.y * d, loc.z + dir.z
				* d, d);
	}

	public Ray moveR(float d) {
		return new Ray(move(d), dir);
	}

	public Ray reflect(Vector o, Vector norm) {
		Vector newDir = Vector.subtract(dir, norm, 2 * Vector.dot(dir, norm));
		return new Ray(Vector.add(o, 1, newDir, .01f), newDir);
	}
}
