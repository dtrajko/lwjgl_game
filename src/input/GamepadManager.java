package input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import entities.Player;
import extra.Camera;
import loaders.SceneLoader;
import utils.DisplayManager;

public class GamepadManager {

	private static Controller gamepad;
	private static boolean gamePadEnabled = false;

	private static Camera camera = null;
	private static Player player = null;

	public static void init() {

		try {
			Controllers.create();
			Controllers.poll();
		} catch (LWJGLException e) {
			e.printStackTrace();
			return;
		}

		for (int i = 0; i < Controllers.getControllerCount(); i++) {
			gamepad = Controllers.getController(i);
			// System.out.println("GamepadManager controller name: " + gamepad.getName());
			for (int b = 0; b < gamepad.getButtonCount(); b++) {
				// System.out.println("Button " + b + ": " + gamepad.getButtonName(b));
			}
			for (int a = 0; a < gamepad.getAxisCount(); a++) {
				// System.out.println("Axis " + a + ": " + gamepad.getAxisName(a));
			}
		}
	}

	public static Controller getGamepad() {
		return gamepad;
	}

	public static void handleInput() {

		if (gamepad == null) {
			return;
		}

		try {
			gamepad.poll();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (camera == null) {
			camera = (Camera) SceneLoader.getScene().getCamera();
		}

		if (player == null) {
			player = SceneLoader.getScene().getAnimatedPlayer();
		}

		// gamepad movement
		if (gamepad.isButtonPressed(7)) {
			gamePadEnabled = true;
		}
		if (gamePadEnabled) {
			if (gamepad.getAxisValue(4) < -0.2) {
				player.setCurrentSpeed(player.getRunSpeed() * -gamepad.getAxisValue(4) * 1.5f);
			}
			if (gamepad.getAxisValue(4) > 0.2) {
				player.setCurrentSpeed(-player.getRunSpeed() * gamepad.getAxisValue(4) * 1.5f);
			}
			if (gamepad.getAxisValue(1) < -0.5) {
				player.setCurrentTurnSpeed(player.getTurnSpeed());
			}
			if (gamepad.getAxisValue(1) > 0.5) {
				player.setCurrentTurnSpeed(-player.getTurnSpeed());
			}
			if (gamepad.getAxisValue(2) < -0.5) {
				float pitch = camera.getPitch();
				camera.setPitch(pitch -= 0.5f);
			}
			if (gamepad.getAxisValue(2) > 0.5) {
				float pitch = camera.getPitch();
				camera.setPitch(pitch += 0.5f);
			}
			if (gamepad.getAxisValue(3) < -0.2) {
				float angleAroundPlayer = camera.getAngleAroundPlayer();
				camera.setAngleAroundPlayer(angleAroundPlayer += gamepad.getAxisValue(3) * 2);
			}
			if (gamepad.getAxisValue(3) > 0.2) {
				float angleAroundPlayer = camera.getAngleAroundPlayer();
				camera.setAngleAroundPlayer(angleAroundPlayer += gamepad.getAxisValue(3) * 2);
			}
			if (gamepad.isButtonPressed(4) || gamepad.isButtonPressed(5)) {
				player.jump();
			}
			if (gamepad.isButtonPressed(0)) {
				DisplayManager.toggleFullScreen();
			}
			if (gamepad.isButtonPressed(1)) {
				camera.togglePerspective();
			}
		}
	}
}
