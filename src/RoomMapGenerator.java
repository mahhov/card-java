import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RoomMapGenerator extends MapGenerator {
	private static final double CREATE_CHANCE = .5, CONNECT_CHANCE = .1, MIN_DENSITY = .3;
	private static final int ROOM_SIZE = 12, DOOR_OFFSET = ROOM_SIZE / 4;
	private int ROOM_WIDTH, ROOM_LENGTH;
	private Room[][] roomMap;
	private int count;
	
	public int[][][] generate(int width, int length, int height) {
		ROOM_WIDTH = width / ROOM_SIZE;
		ROOM_LENGTH = length / ROOM_SIZE;
		
		while (1. * count / (width * length) < MIN_DENSITY)
			createRooms();
		joinBigRooms();
		translateToMap();
		setSpawn();
		// print();
		paintRoomMap();
		paintMap();
		
		return map;
	}
	
	private void createRooms() {
		roomMap = new Room[ROOM_WIDTH][ROOM_LENGTH];
		LList<Room> pending = new LList<>();
		pending.addHead(new Room(ROOM_WIDTH / 2, ROOM_LENGTH / 2));
		
		while (!pending.isEmpty()) {
			Room room = pending.removeTail();
			if (roomMap[room.x][room.y] == null) {
				count++;
				roomMap[room.x][room.y] = room;
				// left
				if (room.x > 0)
					if (roomMap[room.x - 1][room.y] == null) {
						if (randFlip(CREATE_CHANCE)) { // generate
							Room next = new Room(room.x - 1, room.y);
							room.left = true;
							next.right = true;
							pending.addHead(next);
						}
					} else if (!room.left && randFlip(CONNECT_CHANCE)) { // connect
						roomMap[room.x - 1][room.y].right = true;
						room.left = true;
					}
				
				// right
				if (room.x < ROOM_WIDTH - 1)
					if (roomMap[room.x + 1][room.y] == null) {
						if (randFlip(CREATE_CHANCE)) { // generate
							Room next = new Room(room.x + 1, room.y);
							room.right = true;
							next.left = true;
							pending.addHead(next);
						}
					} else if (!room.right && randFlip(CONNECT_CHANCE)) { // connect
						roomMap[room.x + 1][room.y].left = true;
						room.right = true;
					}
				
				// top
				if (room.y > 0)
					if (roomMap[room.x][room.y - 1] == null) {
						if (randFlip(CREATE_CHANCE)) { // generate
							Room next = new Room(room.x, room.y - 1);
							room.top = true;
							next.bottom = true;
							pending.addHead(next);
						}
					} else if (!room.top && randFlip(CONNECT_CHANCE)) { // connect
						roomMap[room.x][room.y - 1].bottom = true;
						room.top = true;
					}
				
				// bottom
				if (room.y < ROOM_LENGTH - 1)
					if (roomMap[room.x][room.y + 1] == null) {
						if (randFlip(CREATE_CHANCE)) { // generate
							Room next = new Room(room.x, room.y + 1);
							room.bottom = true;
							next.top = true;
							pending.addHead(next);
						}
					} else if (!room.bottom && randFlip(CONNECT_CHANCE)) { // connect
						roomMap[room.x][room.y + 1].top = true;
						room.bottom = true;
					}
			} else {
				roomMap[room.x][room.y].left = roomMap[room.x][room.y].left || room.left;
				roomMap[room.x][room.y].right = roomMap[room.x][room.y].right || room.right;
				roomMap[room.x][room.y].top = roomMap[room.x][room.y].top || room.top;
				roomMap[room.x][room.y].bottom = roomMap[room.x][room.y].bottom || room.bottom;
			}
		}
	}
	
	private void joinBigRooms() {
		for (int x = 0; x < ROOM_WIDTH - 1; x++)
			for (int y = 0; y < ROOM_LENGTH - 1; y++)
				if (roomMap[x][y] != null && roomMap[x + 1][y] != null && roomMap[x][y + 1] != null && roomMap[x + 1][y + 1] != null
						&& !roomMap[x][y].subBigRoom && !roomMap[x + 1][y].subBigRoom && !roomMap[x][y + 1].subBigRoom && !roomMap[x + 1][y + 1].subBigRoom) {
					int i = 0;
					if (roomMap[x][y].isTopLeftCorner())
						i++;
					if (roomMap[x + 1][y].isTopRightCorner())
						i++;
					if (roomMap[x][y + 1].isBottomLeftCorner())
						i++;
					if (roomMap[x + 1][y + 1].isBottomRightCorner())
						i++;
					if (i >= 3) {
						roomMap[x][y].bigRoom = true;
						roomMap[x][y].right = false;
						roomMap[x][y].bottom = false;
						roomMap[x + 1][y].subBigRoom = true;
						roomMap[x + 1][y].left = false;
						roomMap[x + 1][y].bottom = false;
						roomMap[x][y + 1].subBigRoom = true;
						roomMap[x][y + 1].right = false;
						roomMap[x][y + 1].top = false;
						roomMap[x + 1][y + 1].subBigRoom = true;
						roomMap[x + 1][y + 1].left = false;
						roomMap[x + 1][y + 1].top = false;
					}
				}
	}
	
	private void translateToMap() {
		map = new int[ROOM_WIDTH * ROOM_SIZE * 2 + ROOM_SIZE][ROOM_LENGTH * ROOM_SIZE * 2 + ROOM_SIZE][1];
		for (int x = 0; x < ROOM_WIDTH; x++)
			for (int y = 0; y < ROOM_LENGTH; y++) {
				Room room = roomMap[x][y];
				if (room != null) {
					int left = x * ROOM_SIZE * 2 + ROOM_SIZE, top = y * ROOM_SIZE * 2 + ROOM_SIZE, size = ROOM_SIZE, right = left + size, bottom = top + size;
					if (room.bigRoom) {
						size = ROOM_SIZE * 3;
						right = left + size;
						bottom = top + size;
					}
					
					if (!room.subBigRoom)
						rectMap(left, top, right, bottom, 1, 1);
					
					if (room.left)
						rectMap(left - ROOM_SIZE - 1, top + DOOR_OFFSET, left + 1, top + DOOR_OFFSET * 3, 0, 1);
					if (room.top)
						rectMap(left + DOOR_OFFSET, top - ROOM_SIZE - 1, left + DOOR_OFFSET * 3, top + 1, 1, 0);
				}
			}
	}
	
	private void rectMap(int left, int top, int right, int bottom, int valueLeftRight, int valueTopBottom) {
		for (int xx = left; xx < right; xx++) {
			map[xx][top][0] = valueTopBottom;
			map[xx][bottom - 1][0] = valueTopBottom;
		}
		for (int yy = top; yy < bottom; yy++) {
			map[left][yy][0] = valueLeftRight;
			map[right - 1][yy][0] = valueLeftRight;
		}
	}
	
	private void setSpawn() {
		spawn = new Pos(ROOM_WIDTH / 2, ROOM_LENGTH / 2);
	}
	
	private void print() {
		String s = "";
		for (int y = 0; y < ROOM_LENGTH; y++) {
			for (int x = 0; x < ROOM_WIDTH; x++)
				if (roomMap[x][y] == null)
					s += "  ";
				else {
					if (roomMap[x][y].left)
						s += "-";
					else
						s += " ";
					if (x == ROOM_WIDTH / 2 && y == ROOM_LENGTH / 2)
						s += "8";
					else if (roomMap[x][y].isCorner())
						s += "Z";
					else
						s += "X";
				}
			s += "\n";
			
			for (int x = 0; x < ROOM_WIDTH; x++)
				if (roomMap[x][y] != null && roomMap[x][y].bottom)
					s += " |";
				else
					s += "  ";
			s += "\n";
		}
		System.out.println(s);
	}
	
	private void paintRoomMap() {
		BufferedImage image = new BufferedImage(ROOM_WIDTH * ROOM_SIZE * 2 + ROOM_SIZE, ROOM_LENGTH * ROOM_SIZE * 2 + ROOM_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D brush = (Graphics2D) image.getGraphics();
		brush.setColor(Color.WHITE);
		
		for (int x = 0; x < ROOM_WIDTH; x++)
			for (int y = 0; y < ROOM_LENGTH; y++) {
				Room room = roomMap[x][y];
				if (room != null) {
					int left = x * ROOM_SIZE * 2 + ROOM_SIZE, top = y * ROOM_SIZE * 2 + ROOM_SIZE, size = ROOM_SIZE, right = left + size, bottom = top + size;
					if (room.bigRoom) {
						size = ROOM_SIZE * 3;
						right = left + size;
						bottom = top + size;
					}
					
					if (x == ROOM_WIDTH / 2 && y == ROOM_LENGTH / 2)
						brush.fillRect(left, top, size, size);
					else if (!room.subBigRoom)
						brush.drawRect(left, top, size, size);
					
					if (room.right)
						brush.drawRect(right, top + DOOR_OFFSET, ROOM_SIZE, DOOR_OFFSET * 2);
					if (room.bottom)
						brush.drawRect(left + DOOR_OFFSET, bottom, DOOR_OFFSET * 2, ROOM_SIZE);
				}
			}
		
		try {
			File outputfile = new File("roomMap.png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
		}
	}
	
	private void paintMap() {
		BufferedImage image = new BufferedImage(map.length, map[0].length, BufferedImage.TYPE_INT_RGB);
		
		for (int x = 0; x < map.length; x++)
			for (int y = 0; y < map[0].length; y++)
				if (map[x][y][0] == 1)
					image.setRGB(x, y, Color.WHITE.getRGB());
		
		try {
			File outputfile = new File("map.png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
		}
	}
	
	private static class Room {
		private int x, y;
		private boolean left, right, top, bottom;
		private boolean bigRoom, subBigRoom;
		
		private Room(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		private boolean isCorner() {
			return (bottom || top) && (right && top);
		}
		
		private boolean isTopLeftCorner() {
			return bottom && right;
		}
		
		private boolean isTopRightCorner() {
			return bottom && left;
		}
		
		private boolean isBottomLeftCorner() {
			return top && right;
		}
		
		private boolean isBottomRightCorner() {
			return top && left;
		}
	}
}