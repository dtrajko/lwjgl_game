package main;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.opengl.Display;

import input.GamepadManager;
import loaders.LoaderSettings;
import loaders.SceneLoader;
import loaders.SceneLoaderFactory;
import particles.ParticleMaster;
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

		RenderEngine engine = RenderEngine.init();
		SceneLoader loader = SceneLoaderFactory.createSceneLoader();
		Scene scene = loader.loadScene(new MyFile(LoaderSettings.RES_FOLDER), new MyFile(LoaderSettings.RES_FOLDER, "Socuwan Scene"));
		ParticleMaster.init(loader.getLoader(), scene.getCamera().getProjectionMatrix());
		GamepadManager.init();

		DisplayManager.startFPS();

		while (!Display.isCloseRequested()) {
			scene.update();
			engine.renderScene(scene);
			engine.update();
		}

		scene.delete();
		engine.close();

	}
}
