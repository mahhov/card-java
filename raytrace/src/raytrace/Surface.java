package raytrace;

public class Surface {
	// must be convex polygon
	private int n;
	private Vector p[];
	Vector norm;

	private IntersectConst precompute[];

	Surface(Vector... p) {
		n = p.length;
		this.p = p;
		norm = Vector.cross(Vector.subtract(p[1], p[0]),
				Vector.subtract(p[2], p[0]), true);
		precompute = new IntersectConst[n - 2];
		for (int i = 0; i < n - 2; i++)
			precompute[i] = new IntersectConst(p[0], p[i + 1], p[i + 2]);
	}

	public void flipNormal() {
		// norm = Vector.subtract(new Vector(), norm);
		norm = new Vector(-norm.x, -norm.y, -norm.z);
	}

	public Vector intersect(Ray ray, float maxDistance, boolean doubleSide) {
		float denom = Vector.dot(ray.dir, norm);
		// if ray moving in direction of norm, then will hit back side
		if (denom >= .01f && !doubleSide)
			return null;
		float distance = Vector.dot(Vector.subtract(p[0], ray.loc), norm)
				/ denom;
		// if ray moving away from surface, will not collide
		if (distance <= 0 || (distance >= maxDistance && maxDistance != -1))
			return null;

		Vector c = ray.move(distance);

		for (int i = 1; i < n - 1; i++) {
			if (intersect(c, precompute[i - 1])) {
				return c;
			}
		}

		return null;
	}

	// is c inside the triangle v0,v1,v2
	private boolean intersect(Vector c, IntersectConst precompute) {
		Vector d = Vector.subtract(c, p[0]);
		float dotd1 = Vector.dot(d, precompute.delta1);
		float dotd2 = Vector.dot(d, precompute.delta2);

		float param1 = (precompute.dot12 * dotd2 - precompute.dot2 * dotd1)
				/ precompute.denom;
		float param2 = (precompute.dot12 * dotd1 - precompute.dot1 * dotd2)
				/ precompute.denom;

		return param1 >= 0 && param2 >= 0 && param1 + param2 <= 1;
	}

	private static class IntersectConst {

		private Vector delta1;
		private Vector delta2;
		private float dot1;
		private float dot2;
		private float dot12;
		private float denom;

		private IntersectConst(Vector v0, Vector v1, Vector v2) {
			delta1 = Vector.subtract(v1, v0);
			delta2 = Vector.subtract(v2, v0);
			dot1 = Vector.dot(delta1, delta1);
			dot2 = Vector.dot(delta2, delta2);
			dot12 = Vector.dot(delta1, delta2);
			denom = dot12 * dot12 - dot1 * dot2;
		}
	}

}