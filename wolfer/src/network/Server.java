package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import message.Message;
import message.Messager;

public class Server {

	final static int port = 2000;
	int connections;
	Socket socket[];
	ObjectOutputStream out[];
	ObjectInputStream in[];
	public boolean disconnect;

	public Server(int connections) {
		this.connections = connections;
		socket = new Socket[connections];
		out = new ObjectOutputStream[connections];
		in = new ObjectInputStream[connections];
	}

	public void connect() {
		for (int i = 0; i < connections; i++) {
			try {
				ServerSocket ss = new ServerSocket(2000);
				String ip = InetAddress.getLocalHost().toString();
				int port = ss.getLocalPort();
				System.out.println("waiting for connection");
				System.out.printf("ip %s, port %d\n", ip, port);
				socket[i] = ss.accept();
				out[i] = new ObjectOutputStream(socket[i].getOutputStream());
				in[i] = new ObjectInputStream(socket[i].getInputStream());
				System.out.printf("connection successful %d of %d\n", i + 1,
						connections);
				ss.close();
				disconnect = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void send(int s, Message m) {
		if (s == -1)
			for (int i = 0; i < connections; i++)
				send(i, m);
		else {
			while (m != null) {
				try {
					m.write(out[s]);
				} catch (Exception e) {
					disconnect = true;
				}
				m = m.nextMessage;
			}
		}
	}

	public Message read(int s) {
		Message head = Messager.read(in[s]);
		Message m = head;
		Message last = head;
		while (m != null) {
			m.nextMessage = Messager.read(in[s]);
			last = m;
			m = m.nextMessage;
		}
		return last;
		// return head;
	}

	public void close() {
		for (int i = 0; i < connections; i++) {
			try {
				in[i].close();
				out[i].close();
				socket[i].close();
			} catch (IOException e) {
			}
		}
	}

}
