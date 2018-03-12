package racetrack;

import loaders.SceneLoader;
import utils.DisplayManager;

public class LapStopwatch {

	private int lapCount = 0;
	private int lapStartTimestamp = 0; // timestamp
	private int currentTimestamp = 0; // timestamp
	private int measuredLapTime = 0; // seconds
	private int actualLapTime = 0; // seconds
	private int bestLapTime = 0; // seconds
	private int penaltySeconds = 0; // seconds
	private int penaltyTime = 0;
	private int previousPenaltyTime = penaltyTime;

	public LapStopwatch() {
		init();
	}
	
	public void init() {
		measuredLapTime = 0;
	}

	public void reset() {
		if (bestLapTime == 0 || actualLapTime < bestLapTime) {
			bestLapTime = actualLapTime;
		}
		lapStartTimestamp = Math.round(DisplayManager.getCurrentTime());
		measuredLapTime = 0;
		actualLapTime = 0;
		penaltySeconds = 0;
		penaltyTime = 0;
		previousPenaltyTime = 0;
		lapCount++;
	}

	public void update(boolean running) {
		if (!running) {
			return;
		}
		currentTimestamp = Math.round(DisplayManager.getCurrentTime());
		measuredLapTime = Math.round((currentTimestamp - lapStartTimestamp) / 1000);
		actualLapTime = measuredLapTime + penaltySeconds;
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
		return actualLapTime;
	}

	public String getLapTime() {
		return formatLapTime(actualLapTime);
	}

	public String getBestLap() {
		return formatLapTime(bestLapTime);
	}

	public int getLapCount() {
		return lapCount;
	}

	public void addPenaltySeconds(int numSeconds) {
		penaltyTime = getCurrentLapTime();
		if (previousPenaltyTime == 0 || penaltyTime - previousPenaltyTime > 5) {
			penaltySeconds += numSeconds;
			previousPenaltyTime = penaltyTime;
			System.out.println(numSeconds + " penalty seconds appended in lap " + lapCount + ". Total penalty seconds: " + penaltySeconds);
		}
	}
}
