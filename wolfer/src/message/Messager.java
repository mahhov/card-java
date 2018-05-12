package message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Messager {

	// static void write(ObjectOutputStream out, Message m) throws IOException {
	// m.write(out);
	// }

	public static Message read(ObjectInputStream in) {
		try {
			if (in.available() <= 0)
				return null;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			byte code = in.readByte();
			// System.out.println("decoded " + code);
			switch (code) {
			case Controls.CODE:
				return new Controls(in);
			case MoveCharTo.CODE:
				return new MoveCharTo(in);
			case MapLayout.CODE:
				return new MapLayout(in);
			case IdentifyPlayer.CODE:
				return new IdentifyPlayer(in);
			default:
				System.out.println("missing message type in Messager: " + code);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
