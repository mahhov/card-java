package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.JOptionPane;

class Server {

	static final String BROADCAST_IP = "224.0.172.46";
	static final int BROADCAST_PORT = 130;
	static final byte[] BROADCAST_CODE = { 57, 48, 2 };

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ServerBroadcast broadcaster;

	ServerStorage storage;

	private boolean running = true;

	public static void main(String[] args) {
		new Server();
	}

	Server() {
		storage = new ServerStorage();

		connectToClient();

		storage.setConnection(clientSocket);

		running = true;
		while (running)
			loop();

		exit();
	}

	private void connectToClient() {
		try {
			serverSocket = new ServerSocket(0);
			broadcaster = new ServerBroadcast();
			broadcaster.setBroadcastMessage(Util.intToByte(serverSocket
					.getLocalPort()));
			new Thread(broadcaster).start();

			waitConnection();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void waitConnection() {
		try {
			serverSocket.setSoTimeout(6000);
			clientSocket = serverSocket.accept();
			broadcaster.stop();
		} catch (SocketTimeoutException e) {
			JOptionPane.showMessageDialog(null,
					"Connection Timeout: no client found.");
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loop() {
		storage.reset();
		storage.updateChanges();
		storage.sendChanges();
		Util.sleep(10);
	}

	private void exit() {
		try {
			storage.close();
			clientSocket.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
