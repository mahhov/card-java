package world;

public class TicTacToeGrid {

	public static final byte EMPTY = 0, TEAM_X = 1, TEAM_O = 2, DRAW = 3;
	byte[][] grid;
	byte turn;
	byte piecesPlaced;
	byte winner;

	public TicTacToeGrid() {
		winner = EMPTY;
		turn = TEAM_X;
		grid = new byte[3][3];
	}

	boolean place(byte team, byte x, byte y) {
		if (winner != EMPTY || team == EMPTY || grid[x][y] != EMPTY)
			return false;
		piecesPlaced++;
		grid[x][y] = team;
		return true;
	}

	void nextTurn() {
		if (turn == TEAM_X)
			turn = TEAM_O;
		else
			turn = TEAM_X;
	}

	boolean checkGameEnded() {
		if (piecesPlaced == 9)
			winner = DRAW;

		// _|_|* . _|*|_ . _|_|_
		// _|*|_ . _|*|_ . *|*|*
		// *| | . . |*| . . | |

		byte team = grid[0][0];
		if (team != EMPTY
				&& ((team == grid[1][0] && team == grid[2][0])
						|| (team == grid[0][1] && team == grid[0][2]) || (team == grid[1][1] && team == grid[2][2])))
			winner = team;

		// _|_|* . _|*|_ . _|_|_
		// _|*|_ . _|*|_ . *|*|*
		// *| | . . |*| . . | |

		team = grid[2][2];
		if (team != EMPTY
				&& ((team == grid[2][1] && team == grid[2][0]) || (team == grid[1][2] && team == grid[0][2])))
			winner = team;

		// _|_|* . _|*|_ . _|_|_
		// _|*|_ . _|*|_ . *|*|*
		// *| | . . |*| . . | |

		team = grid[1][1];
		if (team != EMPTY
				&& ((team == grid[2][0] && team == grid[0][2])
						|| (team == grid[1][0] && team == grid[1][2]) || (team == grid[0][1] && team == grid[2][1])))
			winner = team;

		return winner != EMPTY;
	}

}
