package racetrack;

import entities.Player;
import utils.DisplayManager;

public class Racetrack {

	private LapStopwatch stopwatch;
	private boolean raceInProgress = false;
	private Player player = null;

	public Racetrack(Player player) {
		this.player = player;
		this.stopwatch = new LapStopwatch();
	}

	public LapStopwatch getStopwatch() {
		return this.stopwatch;
	}

	public void update() {
		if (player.getPosition().x > 105 && player.getPosition().x < 125 &&
				player.getPosition().z > 165 && player.getPosition().z < 175) {
			if (raceInProgress == false || stopwatch.getCurrentLapTime() > 30) {
				stopwatch.reset();
				raceInProgress = true;
			}
		}
		stopwatch.update(raceInProgress);			
	}
}
