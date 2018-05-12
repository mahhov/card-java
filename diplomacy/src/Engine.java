public class Engine {

	Nation[] nation;
	City[] city;
	Control control;
	Painter painter;

	boolean running;

	int citySelected, nationSelected, attackSelected, attackCount;
	Treaty treaty;

	int norder;
	Order[] order;

	public static void main(String[] args) {
		new Engine(2, 4);
	}

	Engine(int numNations, int numCities) {
		control = new Control();
		painter = new Painter(control);

		nation = new Nation[numNations];
		nation[0] = new Nation("Player", Nation.STANCE_PLAYER, 2);
		for (int i = 1; i < numNations; i++)
			nation[i] = new Nation("Nation " + i, Nation.STANCE_NORMAL, 2);

		city = new City[numCities];
		for (int i = 0; i < numCities; i++) {
			city[i] = new City("City " + i, 1000, 100, i * numNations
					/ numCities);
		}

		citySelected = -1;
		nationSelected = -1;
		attackSelected = -1;
		attackCount = 0;
		order = new Order[100];
		updateCities();

		running = true;
		while (running) {
			loop();
			painter.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.exit(0);
	}

	void loop() {
		if (citySelected == -1 && nationSelected == -1) {
			paintOverviewScreen();
		}

		else if (nationSelected != -1) {
			paintNationScreen();
		}

		else if (citySelected != -1) {
			paintCityScreen();
		}

	}

	void updateCities() {
		for (int i = 0; i < norder; i++) {
			int count = Math.min(order[i].count, city[order[i].from].power);
			city[order[i].from].power -= count;
			System.out.println(city[order[i].from].power);
			if (city[order[i].to].owner == city[order[i].from].owner)
				city[order[i].to].power += count;
			else {
				if (city[order[i].to].stance == City.STANCE_DEFENSE)
					city[order[i].to].power -= count * .6f;
				else
					city[order[i].to].power -= count * .8f;
				if (city[order[i].to].power < 0) {
					city[order[i].to].power = -city[order[i].to].power;
					city[order[i].to].owner = order[i].from;
				}
			}
		}
		norder = 0;

		for (int i = 0; i < nation.length; i++) {
			nation[i].oldPower = nation[i].power;
			nation[i].power = 0;
			nation[i].oldMoney = nation[i].money;
			nation[i].oldWealth = nation[i].wealth;
			nation[i].wealth = 0;
		}

		// TAX
		// income 10%
		// growth 0.5%

		// GROWTH
		// income 5%
		// growth 1.3%

		// NORMAL
		// income 5%
		// growth 1%
		// war 80%

		// TRAIN
		// power +10

		// DEFENSE
		// war %60

		for (int i = 0; i < city.length; i++) {
			// city[i].power -= city[i].powerLeaving;
			city[i].powerLeaving = 0;
			if (city[i].stance == City.STANCE_TAX) {
				nation[city[i].owner].money += city[i].wealth * .1;
				city[i].wealth *= 1.005;
			} else {
				nation[city[i].owner].money += city[i].wealth * .05;
				if (city[i].stance == City.STANCE_GROWTH)
					city[i].wealth *= .013;
				else {
					city[i].wealth *= .01;
					if (city[i].stance == City.STANCE_TRAIN)
						city[i].power += 10;
				}
			}
			nation[city[i].owner].wealth += city[i].wealth;
			nation[city[i].owner].power += city[i].power;
		}
	}

	void paintOverviewScreen() {
		OverviewScreen screen = new OverviewScreen(painter.getBrush(),
				control.getData());
		int row = 0, column = 0;

		for (int i = 0; i < city.length; i++) {
			if (screen.drawCenterBox(row, column)) {
				citySelected = i;
				attackSelected = -1;
			}

			screen.drawCenterText(city[i].name, row, column, 0, 0);
			screen.drawCenterText("wealth: " + city[i].wealth, row, column, 1,
					0);
			screen.drawCenterText(nation[city[i].owner].name, row, column, 0, 1);
			screen.drawCenterText("power: " + city[i].power, row, column, 1, 1);

			row += column == 1 ? 1 : 0;
			column = 1 - column;
		}

		for (int i = 0; i < nation.length; i++) {
			if (screen.drawRightBox(i)) {
				nationSelected = i;
				treaty = new Treaty();
			}
			screen.drawRightText(nation[i].name, i);
		}

		// end turn
		if (screen.drawBottomBox("End Turn")) {
			for (int i = 0; i < nation.length; i++)
				updateNation(i);
			updateCities();
		}

	}

	void updateNation(int i) {
		Nation n = nation[i];

	}

	void paintNationScreen() {
		NationScreen screen = new NationScreen(painter.getBrush(),
				control.getData());

		Nation n = nation[nationSelected];
		screen.drawBoxedTopText(n.name);

		screen.drawBoxes();

		screen.drawSideText(nation[0].name, 0, 0);
		screen.drawSideText("Wealth " + nation[0].oldWealth + " -> "
				+ nation[0].wealth, 1, 0);
		screen.drawSideText("Power " + nation[0].oldPower + " -> "
				+ nation[0].power, 2, 0);
		screen.drawSideText("Money " + nation[0].oldMoney + " -> "
				+ nation[0].money, 3, 0);
		screen.drawSideText("Respect " + nation[0].respect, 4, 0);
		screen.drawSideText("Friendly " + nation[0].friendly[nationSelected],
				5, 0);

		screen.drawSideText(nation[nationSelected].name, 0, 1);
		screen.drawSideText("Wealth " + nation[nationSelected].oldWealth
				+ " -> " + nation[0].wealth, 1, 1);
		screen.drawSideText("Power " + nation[nationSelected].oldPower + " -> "
				+ nation[0].power, 2, 1);
		screen.drawSideText("Money " + nation[nationSelected].oldMoney + " -> "
				+ nation[0].money, 3, 1);
		screen.drawSideText("Respect " + nation[nationSelected].respect, 4, 1);
		screen.drawSideText("Friendly " + nation[nationSelected].friendly[0],
				5, 1);

		if (screen.drawLeftText("Alliance", 0))
			treaty.addTreaty(0);
		if (screen.drawLeftText("Peace", 1))
			treaty.addTreaty(1);
		if (screen.drawLeftText("War", 2))
			treaty.addTreaty(2);
		if (screen.drawLeftText("Trade", 3))
			treaty.addTreaty(3);
		if (screen.drawLeftText("Join Alliance", 4))
			treaty.selectTreaty(4);
		if (screen.drawLeftText("Join War", 5))
			treaty.selectTreaty(5);
		if (screen.drawLeftText("Join Peace", 6))
			treaty.selectTreaty(6);
		if (screen.drawLeftText("Payment", 7))
			treaty.selectTreaty(7);
		if (screen.drawLeftText("Territory", 8))
			treaty.selectTreaty(8);

		if (treaty.selected == -1) {
			int row = 0;
			for (int i = 0; i < treaty.treaty.length; i++) {
				if (treaty.treaty[i][0] != 0) {
					if (screen.drawCenterBox(treaty.getText(i), row++))
						treaty.remove(i);
				}
			}
		} else {
		}

		if (screen.drawBottomBox("Back"))
			nationSelected = -1;
	}

	void paintCityScreen() {
		CityScreen screen = new CityScreen(painter.getBrush(),
				control.getData());

		City c = city[citySelected];

		screen.drawBoxedTopText(c.name + " (" + nation[c.owner].name + ")");

		screen.drawCenterBox("Wealth: " + c.wealth, 0, 0, false);
		screen.drawCenterBox("Power: " + c.power + " (" + c.powerLeaving + ")",
				0, 1, false);
		if (c.owner == 0) {
			if (screen
					.drawCenterBox("Stance: " + c.getStanceName(), 0, 2, true))
				c.toggleStance();
			if (screen.drawCenterBox("Attack", 0, 3, true))
				if (attackSelected == -1) {
					attackCount = 0;
					control.getNum();
					attackSelected = citySelected == 0 ? 1 : citySelected - 1;
				} else {
					if (attackCount != 0) {
						order[norder++] = new Order(citySelected,
								attackSelected, attackCount);
						c.powerLeaving += attackCount;
					}
					attackSelected = -1;
				}
		} else
			screen.drawCenterBox("Stance: " + c.getStanceName(), 0, 2, false);

		if (attackSelected != -1) {
			if (screen.drawCenterBox("Attack " + city[attackSelected].name, 1,
					2, (citySelected != 0 && citySelected != city.length - 1)))
				attackSelected = citySelected * 2 - attackSelected;
			if (screen.drawCenterBox("Power " + attackCount, 1, 3, true)) {
				if (attackCount == c.power - c.powerLeaving)
					attackCount = 0;
				else
					attackCount += 10;
				if (attackCount > c.power - c.powerLeaving)
					attackCount = c.power - c.powerLeaving;
			}
			int num = control.getNum();
			if (num != -1) {
				if (num != -2)
					attackCount = attackCount * 10 + num;
				else
					attackCount /= 10;
				if (attackCount > c.power - c.powerLeaving)
					attackCount = c.power - c.powerLeaving;
			}
		}

		if (screen.drawBottomBox("Back"))
			citySelected = -1;
	}
}
