package message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IdentifyPlayer extends Message {
	public static final byte CODE = 3;

	public boolean wolf;

	public IdentifyPlayer(ObjectInputStream in) {
		read(in);
	}

	public IdentifyPlayer(boolean wolf) {
		this.wolf = wolf;
	}

	void read(ObjectInputStream in) {
		try {
			wolf = in.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(ObjectOutputStream out) throws IOException {
		// try {
		out.writeByte(CODE);
		out.writeBoolean(wolf);
		out.flush();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	public byte getCode() {
		return CODE;
	}

}
