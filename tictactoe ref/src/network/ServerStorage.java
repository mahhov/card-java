package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import message.Message;
import message.Messager;
import player.Player;
import world.TicTacToeGrid;
import world.World;

public class ServerStorage {

	private World world;
	private Player serverPlayer;
	DataInputStream clientIn;
	DataOutputStream clientOut;

	final int clientBufferSize = 10;
	private Message[] clientInput;
	private Message[] serverInput;
	private Message[] changes;

	ServerStorage() {
		world = new World();
		serverPlayer = new Player(TicTacToeGrid.TEAM_X);
	}

	void setConnection(Socket clientSocket) {
		try {
			clientOut = new DataOutputStream(clientSocket.getOutputStream());
			clientIn = new DataInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void updateChanges() {
		changes = world.update(clientInput, serverInput);
	}

	void sendChanges() {
		for (Message c : changes)
			if (c != null) {
				// send changes to client
				c.write(clientOut);
				// send changes to server
				serverPlayer.recieveChange(c);
			}
	}

	void reset() {
		serverPlayer.update();

		changes = null;

		// receive server input
		serverInput = serverPlayer.getInputMessages();

		// receive client input
		clientInput = new Message[clientBufferSize];
		int i = 0;
		try {
			while (clientIn.available() != 0 && i < clientBufferSize) {
				clientInput[i] = Messager.read(clientIn);
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void close() {
		try {
			clientIn.close();
			clientOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
