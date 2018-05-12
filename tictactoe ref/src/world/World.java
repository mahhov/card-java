package world;

import message.EndGame;
import message.Message;
import message.PlacePiece;

public class World {
	// LinkedSetElement dynamicItem;
	// LinkedSetElement staticItem;

	TicTacToeGrid grid;

	public World() {
		grid = new TicTacToeGrid();
	}

	public Message[] update(Message[] inputClientPlayer,
			Message[] inputServerPlayer) {
		// init possible changes
		Message[] change = new Message[2];

		// listen to server
		PlacePiece input = null;
		if (inputServerPlayer.length > 0
				&& inputServerPlayer[0] instanceof PlacePiece) {
			input = (PlacePiece) inputServerPlayer[0];
			if (grid.turn != input.team)
				input = null;
		}
		// listen to client
		if (input == null && inputClientPlayer.length > 0
				&& inputClientPlayer[0] instanceof PlacePiece) {
			input = (PlacePiece) inputClientPlayer[0];
			if (grid.turn != input.team)
				input = null;
		}

		// attempt to make move
		if (input != null && grid.place(input.team, input.x, input.y)) {
			change[0] = input;
			grid.nextTurn();

			// check for win or draw
			if (grid.checkGameEnded())
				change[1] = new EndGame(grid.winner);
		}

		return change;
	}

}
