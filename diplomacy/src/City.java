public class City {

	public final static int STANCE_GROWTH = 0, STANCE_TAX = 1,
			STANCE_DEFENSE = 2, STANCE_TRAIN = 3;
	int stance;

	String name;
	int wealth;
	int power;
	int owner;
	int powerLeaving;

	City(String name, int wealth, int military, int owner) {
		this.name = name;
		this.wealth = wealth;
		this.power = military;
		this.owner = owner;
	}

	String getStanceName() {
		switch (stance) {
		case STANCE_GROWTH:
			return "growth";
		case STANCE_TAX:
			return "tax";
		case STANCE_DEFENSE:
			return "defense";
		case STANCE_TRAIN:
			return "train";
		default:
			return "?";
		}
	}

	void toggleStance() {
		stance = (stance + 1) % 4;
	}
}