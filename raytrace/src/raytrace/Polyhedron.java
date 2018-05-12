package raytrace;

import raytrace.Scene.IntersectData;

// 3d polygon (concave or convex)

public class Polyhedron {
	private Surface[] surface;
	private int n;
	PolyAttrib attrib;
	float xmin, xmax, ymin, ymax, zmin, zmax;
	MatrixTransformation transformation;

	Polyhedron(Vector ka, Vector kd, Vector ks, float sp, float ref,
			Surface... surface) {
		this.surface = surface;
		n = surface.length;
		attrib = new PolyAttrib(ka, kd, ks, sp, ref);
	}

	Polyhedron(Vector ka, Vector kd, Vector ks, float sp, float ref, int n) {
		attrib = new PolyAttrib(ka, kd, ks, sp, ref);
		surface = new Surface[n];
	}

	void setTransformation(MatrixTransformation mt) {
		transformation = mt;
	}

	void setBounds(float xmin, float xmax, float ymin, float ymax, float zmin,
			float zmax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.zmin = zmin;
		this.zmax = zmax;
	}

	void addSurface(Surface s) {
		surface[n++] = s;
	}

	public boolean isIntersect(Ray ray, float maxDistance) {
		if (transformation != null)
			ray = transformation.applyTransform(ray);

		float distanceBoundBox = intersectBoundBox(ray);
		if (distanceBoundBox < 0
				|| (distanceBoundBox >= maxDistance && maxDistance != -1))
			return false;

		for (int i = 0; i < n; i++) {
			if (surface[i].intersect(ray, maxDistance, true) != null)
				return true;
		}

		return false;
	}

	public IntersectData intersect(Ray ray, float maxDistance) {
		// find (first) intersection with one of surfaces

		if (transformation != null)
			ray = transformation.applyTransform(ray);

		float distanceBoundBox = intersectBoundBox(ray);
		if (distanceBoundBox < 0
				|| (distanceBoundBox >= maxDistance && maxDistance != -1))
			return new IntersectData(null, -1);

		Vector intersect = null, p;
		int s = -1;

		for (int i = 0; i < n; i++) {
			p = surface[i].intersect(ray, maxDistance, false);
			if (p != null) {
				intersect = p;
				maxDistance = p.t;
				s = i;
			}
		}

		return new IntersectData(intersect, s);
	}

	public Vector getColor(Vector view, Vector point, int s, Scene scene,
			Light[] dirLight, int dirLightn, Light[] pointLight, int pointLightn) {
		if (transformation != null) {
			Ray r = transformation.applyTransform(new Ray(point, view));
			view = r.dir;
			point = r.loc;
		}

		// return color of intersection point, or null if no intersection
		// compute color of object point
		// view is direction of ray from camera to point (normalized)
		// point is point on surface of ray intersection

		Vector color = new Vector(point.t); // is t really necessary?? no!

		boolean shadow;

		for (int i = 0; i < dirLightn; i++) {
			Vector l = new Vector(-dirLight[i].pos.x, -dirLight[i].pos.y,
					-dirLight[i].pos.z, true);
			shadow = scene.isIntersection(new Ray(point, l).moveR(.001f), -1);
			dirLight[i].addLight(color, l, view, getNorm(s, point), attrib,
					shadow);
		}

		for (int i = 0; i < pointLightn; i++) {
			Vector l = new Vector(pointLight[i].pos.x - point.x,
					pointLight[i].pos.y - point.y, pointLight[i].pos.z
							- point.z, true);
			shadow = scene.isIntersection(new Ray(point, l).moveR(.001f),
					Vector.distance(pointLight[i].pos.x - point.x,
							pointLight[i].pos.y - point.y, pointLight[i].pos.z
									- point.z) + .001f);
			pointLight[i].addLight(color, l, view, getNorm(s, point), attrib,
					shadow);
		}

		return color;
	}

	// return norm of surface s at point p
	Vector getNorm(int s, Vector point) {
		return surface[s].norm;
	}

	private float intersectBoundBox(Ray ray) {
		if (transformation != null)
			ray = transformation.applyTransform(ray);

		// algorithm from zacharmarz's post @
		// http://gamedev.stackexchange.com/questions/18436/most-efficient-aabb-vs-ray-collision-algorithms

		if (ray.loc.x < xmax && ray.loc.x > xmin && ray.loc.y < ymax
				&& ray.loc.y > ymin)
			return 0;

		float t;
		float t1 = (xmin - ray.loc.x) * ray.dirinv.x;
		float t2 = (xmax - ray.loc.x) * ray.dirinv.x;
		if (t1 > t2) {
			t = t1;
			t1 = t2;
			t2 = t;
		}
		float t3 = (ymin - ray.loc.y) * ray.dirinv.y;
		float t4 = (ymax - ray.loc.y) * ray.dirinv.y;
		if (t3 > t4) {
			t = t3;
			t3 = t4;
			t4 = t;
		}
		float t5 = (zmin - ray.loc.z) * ray.dirinv.z;
		float t6 = (zmax - ray.loc.z) * ray.dirinv.z;
		if (t5 > t6) {
			t = t5;
			t5 = t6;
			t6 = t;
		}

		float tmax = t2;
		if (t4 < tmax)
			tmax = t4;
		if (t6 < tmax)
			tmax = t6;

		float tmin = t1;
		if (t3 > tmin)
			tmin = t3;
		if (t5 > tmin)
			tmin = t5;

		if (tmin > tmax)
			return -1;

		if (tmin < 0)
			return tmax;

		return tmin;
	}

	static class PolyAttrib {
		public Vector ka, kd, ks;
		public float sp, ref;

		private PolyAttrib(Vector ka, Vector kd, Vector ks, float sp, float ref) {
			this.ka = ka;
			this.kd = kd;
			this.ks = ks;
			this.sp = sp;
			this.ref = ref;
		}

	}

}
