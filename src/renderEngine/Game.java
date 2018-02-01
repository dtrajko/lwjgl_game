package renderEngine;

import org.lwjgl.input.Keyboard;

public class Game {

	private boolean isRunning = true;
	
	public void setIsRunning() {
		if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
			this.isRunning = !this.isRunning;
			System.out.println("Game is running: " + isRunning);
		}
	}

	public boolean isRunning() {
		return this.isRunning;
	}
}
