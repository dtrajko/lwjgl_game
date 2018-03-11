package racetrack;

import utils.DisplayManager;

public class LapStopwatch {

	private int lapCount = 0;
	private int lapStartTimestamp = 0; // timestamp
	private int currentTimestamp = 0; // timestamp
	private int currentLapTime = 0; // seconds
	private int bestLapTime = 0; // seconds

	public LapStopwatch() {
		init();
	}
	
	public void init() {
		currentLapTime = 0;
	}

	public void reset() {
		if (bestLapTime == 0 || currentLapTime < bestLapTime) {
			bestLapTime = currentLapTime;
		}
		lapStartTimestamp = Math.round(DisplayManager.getCurrentTime());
		currentLapTime = 0;
		lapCount++;
	}

	public void update(boolean running) {
		if (!running) {
			return;
		}
		currentTimestamp = Math.round(DisplayManager.getCurrentTime());
		currentLapTime = Math.round((currentTimestamp - lapStartTimestamp) / 1000);
		// System.out.println("Current Lap Time: " + formatLapTime(currentLapTime) + " seconds");
	}

	private String formatLapTime(int numSeconds) {
		int minutes = Math.round(numSeconds / 60);
		int seconds = numSeconds %= 60;
		String strMin = minutes < 10 ? "0" + minutes : "" + minutes;
		String strSec = seconds < 10 ? "0" + seconds : "" + seconds;
		String lapTime = strMin + ":" + strSec;
		return lapTime;
	}

	public int getCurrentLapTime() {
		return currentLapTime;
	}

	public String getLapTime() {
		return formatLapTime(currentLapTime);
	}

	public String getBestLap() {
		return formatLapTime(bestLapTime);
	}

	public int getLapCount() {
		return lapCount;
	}
}
