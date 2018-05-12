public class Treaty {

	final int TREATY_NUM = 9;
	final int DEMAND = 1, OFFER = 2;
	// Alliance, 0
	// Peace, 1
	// War, 2
	// Trade, 3
	// Join Alliance 4
	// Join War, 5
	// Join Peace, 6
	// Payment, 7
	// Territory, 8

	int selected, count, demand;
	int[][] treaty;

	Treaty() {
		treaty = new int[TREATY_NUM][2];
		selected = -1;
	}

	void selectTreaty(int i) {
		selected = i;
		count = 0;
		demand = 2;
	}

	void addTreaty(int i) {
		if (i >= 0 && i <= 3) {
			treaty[i][0] = 2;
		} else {
			treaty[i][0] = demand;
			treaty[i][1] = count;
		}

	}

	void remove(int i) {
		treaty[i][0] = 0;
	}

	String getText(int i) {
		
		return "";
	}
}
