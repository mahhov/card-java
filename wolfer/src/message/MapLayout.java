package message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MapLayout extends Message {
	public static final byte CODE = 2;

	public int width, height;
	public int[][] map;

	public MapLayout(ObjectInputStream in) {
		read(in);
	}

	public MapLayout(int width, int height, int[][] map) {
		this.width = width;
		this.height = height;
		this.map = map;
	}

	void read(ObjectInputStream in) {
		try {
			width = in.readInt();
			height = in.readInt();
			map = new int[width][height];
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					map[y][x] = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(ObjectOutputStream out) throws IOException {
		// try {
		out.writeByte(CODE);
		out.writeInt(width);
		out.writeInt(height);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				out.writeInt(map[y][x]);
		out.flush();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	public byte getCode() {
		return CODE;
	}

}
