package renderEngine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector4f;

import entityRenderers.EntityRenderer;
import renderer.AnimatedModelRenderer;
import rendering.TerrainRenderer;
import scene.ICamera;
import scene.Scene;
import skybox.SkyboxRenderer;
import utils.Light;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterRendererAux;

/**
 * This class is in charge of rendering everything in the scene to the screen.
 * @author Karl
 *
 */
public class MasterRenderer {

	private static final Vector4f NO_CLIP = new Vector4f(0, 0, 0, 1);

	private SkyboxRenderer skyRenderer;
	private AnimatedModelRenderer animModelRenderer;
	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	private WaterRenderer waterRenderer;
	private WaterRendererAux waterRendererAux;
	private WaterFrameBuffers waterFbos;

	protected MasterRenderer(AnimatedModelRenderer animModelRenderer, SkyboxRenderer skyRenderer) {
		this.skyRenderer = skyRenderer;
		this.animModelRenderer = animModelRenderer;
	}

	protected MasterRenderer(AnimatedModelRenderer animModelRenderer, EntityRenderer entityRenderer, SkyboxRenderer skyRenderer, 
			WaterRenderer waterRenderer, WaterFrameBuffers waterFbos) {
		this.animModelRenderer = animModelRenderer;
		this.entityRenderer = entityRenderer;
		this.skyRenderer = skyRenderer;
		this.waterRenderer = waterRenderer;
		this.waterFbos = waterFbos;
	}

	protected MasterRenderer(AnimatedModelRenderer animModelRenderer, EntityRenderer entityRenderer, SkyboxRenderer skyRenderer, 
			TerrainRenderer terrainRenderer, WaterRendererAux waterRendererAux, WaterFrameBuffers waterFbos) {
		this.animModelRenderer = animModelRenderer;
		this.entityRenderer = entityRenderer;
		this.skyRenderer = skyRenderer;
		this.terrainRenderer = terrainRenderer;
		this.waterRendererAux = waterRendererAux;
		this.waterFbos = waterFbos;
	}

	protected void renderScene(Scene scene) {
		prepare();
		animModelRenderer.render(scene.getAnimatedPlayer(), scene.getCamera(), scene.getLightDirection());
		skyRenderer.render(scene.getSky(), scene.getCamera());
		// terrainRenderer.render(scene.getTerrain(), scene.getCamera(), scene.getLight(), new Vector4f(0.0f, 0.0f, 0.0f, 100000));
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		renderWaterRefractionPass(scene);
		renderWaterReflectionPass(scene);
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		renderMainPass(scene);
	}

	/**
	 * Clean up when the game is closed.
	 */
	protected void cleanUp() {
		skyRenderer.cleanUp();
		entityRenderer.cleanUp();
	}

	/**
	 * Prepare to render the current frame by clearing the framebuffer.
	 */
	private void prepare() {
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	private void renderWaterReflectionPass(Scene scene){
		waterFbos.bindReflectionFrameBuffer();
		prepare();
		scene.getCamera().reflect(scene.getWaterHeight());
		entityRenderer.render(scene.getReflectedEntities(), scene.getCamera(), scene.getLightDirection(), new Vector4f(0,1,0,0.1f));
		skyRenderer.render(scene.getSky(), scene.getCamera());
		waterFbos.unbindCurrentFrameBuffer();
		scene.getCamera().reflect(scene.getWaterHeight());
	}

	private void renderWaterRefractionPass(Scene scene){
		waterFbos.bindRefractionFrameBuffer();
		prepare();
		entityRenderer.render(scene.getUnderwaterEntities(), scene.getCamera(), scene.getLightDirection(), new Vector4f(0,-1,0, 0));
		waterFbos.unbindCurrentFrameBuffer();
	}

	private void renderMainPass(Scene scene) {
		prepare();
		animModelRenderer.render(scene.getAnimatedPlayer(), scene.getCamera(), scene.getLightDirection());
		skyRenderer.render(scene.getSky(), scene.getCamera());
		entityRenderer.render(scene.getAllEntities(), scene.getCamera(), scene.getLightDirection(), NO_CLIP);
		waterRenderer.render(scene.getWater(), scene.getCamera(), scene.getLightDirection());
	}

	public void renderLowQualityScene(Scene scene, ICamera cubeMapCamera){
		prepare();
		entityRenderer.render(scene.getImportantEntities(), cubeMapCamera, scene.getLightDirection(), NO_CLIP);
		skyRenderer.render(scene.getSky(), cubeMapCamera);
	}
}
