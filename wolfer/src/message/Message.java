package message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Message {

	public Message nextMessage;

	public abstract byte getCode();

	abstract void read(ObjectInputStream in);

	public abstract void write(ObjectOutputStream out) throws IOException;
}
