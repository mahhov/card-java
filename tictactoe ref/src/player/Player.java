package player;

import world.TicTacToeGrid;
import message.EndGame;
import message.Message;
import message.PlacePiece;
import draw.Painter;
import draw.PainterJava;

public class Player {

	final int FRAME_SIZE = 400;

	Map map;
	public Painter painter;
	Control control;

	byte team;

	public Player(byte team) {
		this.team = team;

		map = new Map();
		map.display = team == TicTacToeGrid.TEAM_X ? "Team ReX" : "Team BluO";
		control = new Control(FRAME_SIZE);
		painter = new PainterJava(FRAME_SIZE, control);
	}

	public Message[] getInputMessages() {
		if (control.getMouseState(Control.LEFT) == Control.RELEASE) {
			byte x = (byte) (control.getMouseX() * 3);
			byte y = (byte) (control.getMouseY() * 3);
			PlacePiece move = new PlacePiece(team, x, y);
			return new Message[] { move };
		}
		return new Message[0];
	}

	public void recieveChange(Message change) {
		if (change instanceof PlacePiece)
			recieveChange((PlacePiece) change);
		else if (change instanceof EndGame)
			recieveChange((EndGame) change);
	}

	public void recieveChange(PlacePiece change) {
		map.grid[change.x][change.y] = change.team;
	}

	public void recieveChange(EndGame change) {
		if (change.winner == team)
			map.display = "You Win!";
		else if (change.winner != TicTacToeGrid.DRAW)
			map.display = "You Lose!";
		else
			map.display = "Draw!";
	}

	public void update() {
		if (control.getKeyState(Control.KEY_W) == Control.PRESS)
			painter.pause();

		map.paint(painter);
		painter.paint();
	}

}
