package raytrace;

public class Cube extends Polyhedron {
	public Cube(Vector corner, Vector edge1, Vector edge2, Vector edge3,
			Vector ka, Vector kd, Vector ks, float sp, float ref) {
		super(ka, kd, ks, sp, ref, 6);

		Vector c1 = Vector.add(corner, edge1); // x
		Vector c2 = Vector.add(corner, edge2); // y
		Vector c3 = Vector.add(corner, edge3); // z
		Vector c12 = Vector.add(c1, edge2); // x+y
		Vector c13 = Vector.add(c1, edge3); // x+z
		Vector c23 = Vector.add(c2, edge3); // y+z
		Vector c123 = Vector.add(c12, edge3); // x+y+z

		super.addSurface(new Surface(corner, c1, c13, c3)); // front
		super.addSurface(new Surface(c1, c12, c123, c13)); // right
		super.addSurface(new Surface(c12, c2, c23, c123)); // back
		super.addSurface(new Surface(c2, corner, c3, c23)); // left
		super.addSurface(new Surface(c3, c13, c123, c23)); // top
		super.addSurface(new Surface(corner, c2, c12, c1)); // bottom

		xmin = corner.x;
		xmax = corner.x;
		if (edge1.x < 0)
			xmin += edge1.x;
		else
			xmax += edge1.x;
		if (edge2.x < 0)
			xmin += edge2.x;
		else
			xmax += edge2.x;
		if (edge3.x < 0)
			xmin += edge3.x;
		else
			xmax += edge3.x;

		ymin = corner.y;
		ymax = corner.y;
		if (edge1.y < 0)
			ymin += edge1.y;
		else
			ymax += edge1.y;
		if (edge2.y < 0)
			ymin += edge2.y;
		else
			ymax += edge2.y;
		if (edge3.y < 0)
			ymin += edge3.y;
		else
			ymax += edge3.y;

		zmin = corner.z;
		zmax = corner.z;
		if (edge1.z < 0)
			zmin += edge1.z;
		else
			zmax += edge1.z;
		if (edge2.z < 0)
			zmin += edge2.z;
		else
			zmax += edge2.z;
		if (edge3.z < 0)
			zmin += edge3.z;
		else
			zmax += edge3.z;
	}
}
