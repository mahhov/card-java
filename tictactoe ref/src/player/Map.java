package player;

import java.awt.Color;

import world.TicTacToeGrid;
import draw.Line;
import draw.Painter;
import draw.Rectangle;

public class Map {
	byte[][] grid;
	String display;

	Map() {
		grid = new byte[3][3];
		display = "Tic-Tac-Toe";
	}

	void paint(Painter painter) {
		float third = 1f / 3;
		float margin = .1f;

		// grid
		painter.addDraw(new Line(third, 0, third, 1, 3, Color.GRAY));
		painter.addDraw(new Line(2 * third, 0, 2 * third, 1, 3, Color.GRAY));
		painter.addDraw(new Line(0, third, 1, third, 3, Color.GRAY));
		painter.addDraw(new Line(0, 2 * third, 1, 2 * third, 3, Color.GRAY));

		// pieces
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				if (grid[x][y] != TicTacToeGrid.EMPTY) {
					Color color;
					if (grid[x][y] == TicTacToeGrid.TEAM_X)
						color = Color.RED;
					else if (grid[x][y] == TicTacToeGrid.TEAM_O)
						color = Color.BLUE;
					else
						color = Color.GREEN;
					painter.addDraw(new Rectangle(third * x + margin, third * y
							+ margin, third - margin * 2, third - margin * 2,
							color));
				}

		// text
		painter.write(display, 0);
	}
}
