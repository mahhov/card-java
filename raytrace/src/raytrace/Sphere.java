package raytrace;

import raytrace.Scene.IntersectData;

public class Sphere extends Polyhedron {

	// does not take advantage of bound box because it would probably not be
	// much faster than straight up sphere geometry

	private Vector center;
	private float r, rr;

	public Sphere(Vector center, float r, Vector ka, Vector kd, Vector ks,
			float sp, float ref) {
		super(ka, kd, ks, sp, ref, 0);

		this.center = center;
		this.r = r;
		rr = r * r;
	}

	public boolean isIntersect(Ray ray, float maxDistance) {
		if (transformation != null)
			ray = transformation.applyTransform(ray);

		Vector toCenter = Vector.subtract(center, ray.loc);
		float t = Vector.dot(toCenter, ray.dir);

		// ray moves wrong direction or too far
		if (t < 0 || (t >= maxDistance && maxDistance != -1))
			return false;

		float dd = Vector.dot(toCenter, toCenter) - t * t;

		if (dd > rr) // too far from circle
			return false;

		return true;
	}

	public IntersectData intersect(Ray ray, float maxDistance) {
		if (transformation != null)
			ray = transformation.applyTransform(ray);

		Vector p = intersectGeometry(ray, maxDistance);
		if (p != null)
			return new IntersectData(p, 0);
		return new IntersectData(null, -1);
	}

	// point of intersection, or null if no intersection
	private Vector intersectGeometry(Ray ray, float maxDistance) {
		Vector toCenter = Vector.subtract(center, ray.loc);
		float t = Vector.dot(toCenter, ray.dir);

		// ray moves wrong direction or too far
		if (t < 0 || (t - r >= maxDistance && maxDistance != -1))
			return null;

		float dd = Vector.dot(toCenter, toCenter) - t * t;

		if (dd > rr) // too far from circle
			return null;

		if (dd != rr)
			t -= Math.sqrt(rr - dd); // 2 intersections, want nearest
		// else, tangent intersection

		if (t >= maxDistance && maxDistance != -1)
			return null;

		Vector v = Vector.add(ray.loc, 1, ray.dir, t);
		v.t = t;
		return v;
	}

	Vector getNorm(int s, Vector point) {
		return Vector.subtractDivide(point, center, r);
	}
}
