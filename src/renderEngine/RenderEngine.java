package renderEngine;

import org.lwjgl.util.vector.Vector3f;

import environmentMapRenderer.EnviroMapRenderer;
import scene.Scene;
import textures.Texture;
import utils.DisplayManager;

/**
 * This class represents the entire render engine.
 * 
 * @author Karl
 *
 */
public class RenderEngine {

	private static MasterRenderer renderer;

	private RenderEngine(MasterRenderer renderer) {
		RenderEngine.renderer = renderer;
	}

	public static MasterRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Updates the display.
	 */
	public void update() {
		DisplayManager.updateDisplayMode();
		DisplayManager.updateDisplay();
	}

	/**
	 * Renders the scene to the screen.
	 * 
	 * @param scene
	 *            - the game scene.
	 */
	public void renderScene(Scene scene) {
		renderer.renderScene(scene);
	}

	/**
	 * Initializes a new render engine. Creates the display and inits the
	 * renderers.
	 * 
	 * @return
	 */
	public static RenderEngine init() {
		DisplayManager.createDisplay();
		MasterRenderer renderer = new MasterRenderer();
		return new RenderEngine(renderer);
	}

	public void renderEnvironmentMap(Texture enviroMap, Scene scene, Vector3f center){
		EnviroMapRenderer.renderEnvironmentMap(enviroMap, scene, center, renderer);
	}

	/**
	 * Cleans up the renderers and closes the display.
	 */
	public void close() {
		renderer.cleanUp();
		DisplayManager.closeDisplay();
	}
}
