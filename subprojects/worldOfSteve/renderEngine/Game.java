package renderEngine;

import org.lwjgl.input.Keyboard;

import input.InputHelper;

public class Game {

	private static boolean isRunning = true;

	public static void checkIfRunning() {
		if (InputHelper.isKeyPressed(Keyboard.KEY_P)) {
			toggleIsRunning();
			System.out.println("Game is running: " + Game.isRunning);
		}
	}

	public static boolean isRunning() {
		return Game.isRunning;
	}

	public static void setIsRunning(boolean isRunning) {
		Game.isRunning = isRunning;
	}

	public static void toggleIsRunning() {
		Game.isRunning = !Game.isRunning;
	}
}
