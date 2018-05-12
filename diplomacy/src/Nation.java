public class Nation {

	String name;

	public final static int STANCE_NORMAL = 0, STANCE_YES = 1,
			STANCE_PLAYER = -1;
	int stance;

	int money, oldMoney;
	int wealth, oldWealth;
	int power, oldPower;

	int respect;
	int[] friendly; // nation[0].friendly[1] is 0's opinion of 1;
					// nation[k].friendly[k] is stupid

	public Nation(String name, int stance, int nationNum) {
		this.name = name;

		this.stance = stance;

		money = 1000;
		oldMoney = 1000;
		wealth = -1;
		oldWealth = -1;
		power = -1;
		oldPower = -1;

		friendly = new int[nationNum];
	}

}
