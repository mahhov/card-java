package message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Controls extends Message {
	public static final byte CODE = 1;

	public byte up, left, right, down;
	public int mousex, mousey;

	public Controls(ObjectInputStream in) {
		read(in);
	}

	public Controls(byte up, byte left, byte right, byte down, int mousex,
			int mousey) {
		this.up = up;
		this.left = left;
		this.right = right;
		this.down = down;
		this.mousex = mousex;
		this.mousey = mousey;
	}

	void read(ObjectInputStream in) {
		try {
			up = in.readByte();
			left = in.readByte();
			right = in.readByte();
			down = in.readByte();
			mousex = in.readInt();
			mousey = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(ObjectOutputStream out) throws IOException {
		// try {
		out.writeByte(CODE);
		out.writeByte(up);
		out.writeByte(left);
		out.writeByte(right);
		out.writeByte(down);
		out.writeInt(mousex);
		out.writeInt(mousey);
		out.flush();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	public byte getCode() {
		return CODE;
	}

}
