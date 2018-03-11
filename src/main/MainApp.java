package main;

import org.lwjgl.input.Controllers;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import factories.FontFactory;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import input.GamepadManager;
import loaders.LoaderSettings;
import loaders.SceneLoader;
import loaders.SceneLoaderFactory;
import particles.ParticleMaster;
import racetrack.LapStopwatch;
import renderEngine.RenderEngine;
import scene.Scene;
import utils.DisplayManager;
import utils.MyFile;

public class MainApp {

	/**
	 * Initializes the engine and loads the scene. For every frame it updates the
	 * camera, updates the animated entity (which updates the animation),
	 * renders the scene to the screen, and then updates the display. When the
	 * display is close the engine gets cleaned up.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		boolean waterRenderingEnabled = false;

		RenderEngine engine = RenderEngine.init();
		SceneLoader loader = SceneLoaderFactory.createSceneLoader();
		Scene scene = loader.loadSceneRaceTrack(new MyFile(LoaderSettings.RES_FOLDER));
		// Scene scene = loader.loadScene(new MyFile(LoaderSettings.RES_FOLDER), new MyFile(LoaderSettings.RES_FOLDER, "Socuwan Scene"));
		ParticleMaster.init(loader.getLoader(), scene.getCamera().getProjectionMatrix());
		GamepadManager.init();
		DisplayManager.startFPS();

		LapStopwatch stopwatch = SceneLoader.getScene().getRacetrack().getStopwatch();
		FontType font = FontFactory.getFont("candara");
		GUIText guiText = new GUIText("", 2.5f, font, new Vector2f(0.38f, 0.9f), 1f, false);
		guiText.setColour(1.0f, 1.0f, 0.9f);
		int lapTime = 0;
		int previousLapTime = -1;

		while (!Display.isCloseRequested()) {
			
			if (!Controllers.isCreated()) {
				GamepadManager.init();
			}

			scene.update();
			engine.update();
			engine.renderScene(scene, waterRenderingEnabled);

			lapTime = stopwatch.getCurrentLapTime();
			if (lapTime != previousLapTime) {
				String textInfoString = "Lap: " + stopwatch.getLapCount() + "    Lap time: " + stopwatch.getLapTime() + "    Best lap: " + stopwatch.getBestLap();
				guiText.setTextString(textInfoString);
				TextMaster.loadText(guiText);
				previousLapTime = lapTime;
			}
			TextMaster.render();
		}

		scene.delete();
		engine.close();

	}
}
