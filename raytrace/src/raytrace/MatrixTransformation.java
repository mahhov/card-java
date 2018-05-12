package raytrace;

public class MatrixTransformation {
	public float[][] m;

	MatrixTransformation(float[][] m) {
		this.m = invert33(m);
	}

	public Ray applyTransform(Ray ray) {
		return new Ray(apply(ray.loc), apply(ray.dir));
	}

	private Vector apply(Vector v) {
		float x = m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z;
		float y = m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z;
		float z = m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z;
		return new Vector(x, y, z);
	}

	static float[][] remove33(int row, int col, float[][] m) {
		float[][] n = new float[2][2];
		int rr = 0, cc;
		for (int r = 0; r < 3; r++) {
			if (r != row) {
				cc = 0;
				for (int c = 0; c < 3; c++) {
					if (c != col)
						n[rr][cc++] = m[r][c];
				}
				rr++;
			}
		}
		return n;
	}

	static float[][] invert33(float[][] m) {
		float det = det33(m);

		float[][] n = new float[3][3];

		int t = 1;

		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				n[r][c] = t * det22(remove33(c, r, m)) / det;
				t *= -1;
			}
		}

		// float[] row1 = new float[] { det22(remove33(0, 0, m)) / det,
		// det22(remove33(1, 0, m)) / det, det22(remove33(2, 0, m)) / det };

		return n;
	}

	static float det22(float[][] m) {
		return m[0][0] * m[1][1] - m[1][0] * m[0][1];
	}

	static float det33(float[][] m) {
		float s1 = m[0][0] * (m[1][1] * m[2][2] - m[2][1] * m[1][2]);
		float s2 = m[0][1] * (m[1][0] * m[2][2] - m[2][0] * m[1][2]);
		float s3 = m[0][2] * (m[1][0] * m[2][1] - m[2][0] * m[1][1]);
		return s1 - s2 + s3;
	}

}
