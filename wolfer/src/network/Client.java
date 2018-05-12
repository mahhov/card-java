package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.Message;
import message.Messager;

public class Client {

	Socket server;
	ObjectOutputStream out;
	ObjectInputStream in;
	public boolean disconnect;

	public Client() {
	}

	public void connect(String ip) {
		try {
			server = new Socket(ip, Server.port);
			out = new ObjectOutputStream(server.getOutputStream());
			in = new ObjectInputStream(server.getInputStream());
			disconnect = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Message m) {
		while (m != null) {
			try {
				m.write(out);
			} catch (IOException e) {
				disconnect = true;
			}
			m = m.nextMessage;
		}
	}

	public Message read() {
		Message head = Messager.read(in);
		Message m = head;
		while (m != null) {
			m.nextMessage = Messager.read(in);
			m = m.nextMessage;
		}
		return head;
	}

	public void close() {
		try {
			in.close();
			out.close();
			server.close();
		} catch (IOException e) {
		}
	}

}
