package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import message.Message;
import message.Messager;
import player.Player;
import world.TicTacToeGrid;

class Client {

	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;

	private Player player;
	private boolean running;

	public static void main(String[] args) throws IOException {
		new Client();
	}

	Client() {
		player = new Player(TicTacToeGrid.TEAM_O);
		connectToServer();

		running = true;
		while (running)
			try {
				loop();
			} catch (IOException e) {
				e.printStackTrace();
			}

		exit();
	}

	private void connectToServer() {
		try {
			MulticastSocket searchSocket = new MulticastSocket(
					Server.BROADCAST_PORT);
			InetAddress group = InetAddress.getByName(Server.BROADCAST_IP);
			searchSocket.joinGroup(group);

			byte[] buf = new byte[Server.BROADCAST_CODE.length + 4];
			String ip = "";
			int port = -1;

			DatagramPacket packet = null;
			while ((port = getPortIfServerFound(buf)) == -1) {
				packet = new DatagramPacket(buf, buf.length);
				searchSocket.receive(packet);
				Util.sleep(10);
			}

			ip = packet.getAddress().getHostAddress();

			searchSocket.leaveGroup(group);
			searchSocket.close();

			socket = new Socket(ip, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int getPortIfServerFound(byte[] b) {
		if (b.length != Server.BROADCAST_CODE.length + 4)
			return -1;
		for (int i = 0; i < Server.BROADCAST_CODE.length; i++)
			if (b[i] != Server.BROADCAST_CODE[i])
				return -1;
		byte[] p = new byte[4];
		for (int i = 0; i < 4; i++)
			p[i] = b[i + Server.BROADCAST_CODE.length];
		return Util.byteToInt(p);
	}

	private void loop() throws IOException {
		// get changes from server and feed to player
		while (in.available() != 0)
			player.recieveChange(Messager.read(in));

		// update client player display
		player.update();

		// send client player input
		Message[] input = player.getInputMessages();
		for (Message m : input)
			m.write(out);

		Util.sleep(10);
	}

	private void exit() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
