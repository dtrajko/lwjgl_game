package renderEngine;

import org.lwjgl.util.vector.Vector3f;

import entityRenderers.EntityRenderer;
import environmentMapRenderer.EnviroMapRenderer;
import renderer.AnimatedModelRenderer;
import scene.Scene;
import shinyRenderer.ShinyRenderer;
import skybox.SkyboxRenderer;
import textures.Texture;
import utils.DisplayManager;
import water.WaterFrameBuffers;
import water.WaterRenderer;

/**
 * This class represents the entire render engine.
 * 
 * @author Karl
 *
 */
public class RenderEngine {

	private MasterRenderer renderer;

	private RenderEngine(MasterRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Updates the display.
	 */
	public void update() {
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
	 * Cleans up the renderers and closes the display.
	 */
	public void close() {
		renderer.cleanUp();
		DisplayManager.closeDisplay();
	}

	/**
	 * Initializes a new render engine. Creates the display and inits the
	 * renderers.
	 * 
	 * @return
	 */
	public static RenderEngine init() {
		DisplayManager.createDisplay();
		AnimatedModelRenderer animatedModelRenderer = new AnimatedModelRenderer();
		EntityRenderer entityRenderer = new EntityRenderer();
		WaterFrameBuffers waterFbos = new WaterFrameBuffers();
		SkyboxRenderer skyRenderer = new SkyboxRenderer();
		WaterRenderer waterRenderer = new WaterRenderer(waterFbos);
		ShinyRenderer shinyRenderer = new ShinyRenderer();
		MasterRenderer renderer = new MasterRenderer(animatedModelRenderer, entityRenderer, skyRenderer, waterRenderer, waterFbos,
				shinyRenderer);
		return new RenderEngine(renderer);
	}

	public void renderEnvironmentMap(Texture enviroMap, Scene scene, Vector3f center){
		EnviroMapRenderer.renderEnvironmentMap(enviroMap, scene, center, renderer);
	}

}
