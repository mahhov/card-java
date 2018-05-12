package world;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import message.Controls;
import message.IdentifyPlayer;
import network.Server;

public class Host {

	Map map;
	Server server;

	public static void main(String[] args) {
		new Host();
	}

	Host() {
		while (true) {
			server = new Server(2);
			server.connect();

			map = new Map();
			String mapFileName = "map1.png";
			try {
				map.init(ImageIO.read(new File(mapFileName)));
			} catch (IOException e) {
				System.out.println("failed to load " + mapFileName);
				map.init();
			}
			server.send(-1, map.toInitMessage());
			server.send(map.wolfPlayer, new IdentifyPlayer(true));
			server.send(1 - map.wolfPlayer, new IdentifyPlayer(false));

			while (!server.disconnect) {
				map.handleInput(0, (Controls) server.read(0));
				map.handleInput(1, (Controls) server.read(1));
				map.update();
				server.send(-1, map.toUpdateMessage());
				Util.sleep(10);
			}
			server.close();
			System.out.println("Ressetting Host");
		}
	}
}
