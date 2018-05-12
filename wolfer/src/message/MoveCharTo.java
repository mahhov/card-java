package message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MoveCharTo extends Message {
	public static final byte CODE = 0;

	public boolean wolf;
	public float x, y;
	public float dirx, diry;

	public MoveCharTo(ObjectInputStream in) {
		read(in);
	}

	public MoveCharTo(boolean wolf, float x, float y, float dirx, float diry) {
		this.wolf = wolf;
		this.x = x;
		this.y = y;
		this.dirx = dirx;
		this.diry = diry;
	}

	void read(ObjectInputStream in) {
		try {
			wolf = in.readBoolean();
			x = in.readFloat();
			y = in.readFloat();
			dirx = in.readFloat();
			diry = in.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(ObjectOutputStream out) throws IOException {
		// try {
		out.writeByte(CODE);
		out.writeBoolean(wolf);
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(dirx);
		out.writeFloat(diry);
		out.flush();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	public byte getCode() {
		return CODE;
	}

}
