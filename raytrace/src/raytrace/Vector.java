package raytrace;

public class Vector {
	public float x, y, z, t;

	public Vector() {
	}

	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(float x, float y, float z, boolean normalize) {
		float d = distance(x, y, z);
		this.x = x / d;
		this.y = y / d;
		this.z = z / d;
	}

	public Vector(float x, float y, float z, float t) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = t;
	}

	public Vector(float t) {
		this.t = t;
	}

	public Vector(Vector original, Vector add1, float scale1, Vector add2,
			float scale2, boolean normalize) {
		x = original.x + add1.x * scale1 + add2.x * scale2;
		y = original.y + add1.y * scale1 + add2.y * scale2;
		z = original.z + add1.z * scale1 + add2.z * scale2;

		if (normalize) {
			float d = distance(x, y, z);
			x = x / d;
			y = y / d;
			z = z / d;
		}
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	// STATIC MATH METHODS

	public static Vector add(Vector v1, Vector v2) {
		return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	public static Vector add(Vector v1, float w1, Vector v2, float w2) {
		return new Vector(v1.x * w1 + v2.x * w2, v1.y * w1 + v2.y * w2, v1.z
				* w1 + v2.z * w2);
	}

	public static Vector subtract(Vector v1, Vector v2) {
		return new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	public static Vector subtract(Vector v1, float w1, Vector v2) {
		return new Vector(v1.x * w1 - v2.x, v1.y * w1 - v2.y, v1.z * w1 - v2.z);
	}

	public static Vector subtract(Vector v1, Vector v2, float w2) {
		return new Vector(v1.x - v2.x * w2, v1.y - v2.y * w2, v1.z - v2.z * w2);
	}

	public static Vector subtractDivide(Vector v1, Vector v2, float d) {
		return new Vector((v1.x - v2.x) / d, (v1.y - v2.y) / d, (v1.z - v2.z)
				/ d);
	}

	public static float distance(float x, float y, float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public static float dot(Vector v1, Vector v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	public static Vector cross(Vector v1, Vector v2, boolean norm) {
		if (!norm)
			return new Vector(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x
					* v2.z, v1.x * v2.y - v1.y * v2.x);
		else {
			float x = v1.y * v2.z - v1.z * v2.y, y = v1.z * v2.x - v1.x * v2.z, z = v1.x
					* v2.y - v1.y * v2.x;
			float d = distance(x, y, z);
			return new Vector(x / d, y / d, z / d);
		}
	}

	public static float pow(float b, float p) {
		return (float) Math.pow(b, p);
	}

	// public static Vector flipNormalize(Vector v) {
	// return new Vector(-v.x, -v.y, -v.z, true);
	// }

}
