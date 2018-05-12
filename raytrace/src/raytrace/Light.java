package raytrace;

import raytrace.Polyhedron.PolyAttrib;

public class Light {
	public Vector pos; // direction for diriectional lights
	public Vector color;

	public Light(Vector pos, Vector color) {
		this.pos = pos;
		this.color = color;
	}

	void addLight(Vector color, Vector l, Vector v, Vector norm,
			PolyAttrib attrib, boolean shadow) {
		// l is vector to light from point (normalized)
		// v is view ray vector from camera to point (normalized)

		color.x += this.color.x * attrib.ka.x;
		color.y += this.color.y * attrib.ka.y;
		color.z += this.color.z * attrib.ka.z;

		if (shadow)
			return;

		// difusal
		float d = Vector.dot(l, norm);
		if (d < 0)
			d = 0;
		color.x += d * this.color.x * attrib.kd.x;
		color.y += d * this.color.y * attrib.kd.y;
		color.z += d * this.color.z * attrib.kd.z;

		// spectral
		d = -Vector.dot(Vector.subtract(norm, d * 2, l), v);
		if (d < 0)
			d = 0;
		d = Vector.pow(d, attrib.sp);
		color.x += d * this.color.x * attrib.ks.x;
		color.y += d * this.color.y * attrib.ks.y;
		color.z += d * this.color.z * attrib.ks.z;
	}
}
