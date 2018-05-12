package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

class ServerBroadcast implements Runnable {

	private boolean broadcast;
	private byte[] broadcastMessage;

	void setBroadcastMessage(byte[] message) {
		broadcastMessage = new byte[Server.BROADCAST_CODE.length
				+ message.length];
		for (int i = 0; i < Server.BROADCAST_CODE.length; i++)
			broadcastMessage[i] = Server.BROADCAST_CODE[i];

		for (int i = 0; i < message.length; i++)
			broadcastMessage[i + Server.BROADCAST_CODE.length] = message[i];
	}

	public void run() {

		broadcast = true;

		try {
			DatagramSocket searchSocket = new DatagramSocket(0);
			InetAddress group = InetAddress.getByName(Server.BROADCAST_IP);

			DatagramPacket packet = new DatagramPacket(broadcastMessage,
					broadcastMessage.length, group, Server.BROADCAST_PORT);

			while (broadcast) {
				searchSocket.send(packet);
				Util.sleep(10);
			}

			searchSocket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void stop() {
		broadcast = false;
	}

}
