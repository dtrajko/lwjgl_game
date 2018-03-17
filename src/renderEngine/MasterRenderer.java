package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import animatedModelRenderer.AnimatedModelRenderer;
import entities.Entity;
import entityRenderers.EntityRenderer;
import fbos.Attachment;
import fbos.Fbo;
import fbos.RenderBufferAttachment;
import fbos.TextureAttachment;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import interfaces.ICamera;
import interfaces.ITerrain;
import interfaces.ITerrainRenderer;
import models.TexturedModel;
import particles.ParticleMaster;
import scene.Scene;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import sunRenderer.SunRenderer;
import terrains.Terrain;
import utils.Light;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterRendererVao;
import water.WaterTileVao;

/**
 * This class is in charge of rendering everything in the scene to the screen.
 * @author Karl
 *
 */
public class MasterRenderer {

	private static final Vector4f NO_CLIP = new Vector4f(0, 0, 0, 1);

	private static final float REFLECT_OFFSET = 0.1f;
	private static final float REFRACT_OFFSET = 1f;

	public static final float FOV = 70; // field of view angle
	public static final float NEAR_PLANE = 1.0f;
	public static final float FAR_PLANE = 3000;

	private SkyboxRenderer skyRenderer;
	private AnimatedModelRenderer animModelRenderer;
	private EntityRenderer entityRenderer;
	private ITerrainRenderer terrainRenderer;
	private WaterRenderer waterRenderer;
	private WaterFrameBuffers waterFbos;
	private SunRenderer sunRenderer;
	private WaterRendererVao waterRendererVao;
	private GuiRenderer guiRenderer;
	private final Fbo reflectionFbo;
	private final Fbo refractionFbo;
	private ShadowMapMasterRenderer shadowMapRenderer;

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

	protected MasterRenderer() {
		this.waterFbos = new WaterFrameBuffers();
		this.refractionFbo = createWaterFbo(Display.getWidth() / 2, Display.getHeight() / 2, true);
		this.reflectionFbo = createWaterFbo(Display.getWidth(), Display.getHeight(), false);
		this.terrainRenderer = new HeightMapTerrainRenderer();
		// terrainRenderer = new TerrainRenderer(true);
		this.waterRenderer = new WaterRenderer(waterFbos);
		this.waterRendererVao = new WaterRendererVao();
		this.skyRenderer = new SkyboxRenderer();
		this.sunRenderer = new SunRenderer();
		this.guiRenderer = new GuiRenderer();
		this.entityRenderer = new EntityRenderer();
		this.animModelRenderer = new AnimatedModelRenderer();
		this.shadowMapRenderer = new ShadowMapMasterRenderer();
	}

	/**
	 * Clean up when the game is closed.
	 */
	protected void cleanUp() {
		this.animModelRenderer.cleanUp();
		this.entityRenderer.cleanUp();
		this.sunRenderer.cleanUp();
		this.skyRenderer.cleanUp();
		this.waterRendererVao.cleanUp();
		this.waterRenderer.cleanUp();
		this.terrainRenderer.cleanUp();
		this.guiRenderer.cleanUp();
		this.shadowMapRenderer.cleanUp();
		this.waterFbos.cleanUp();
		this.refractionFbo.delete();
		this.reflectionFbo.delete();
		TextMaster.cleanUp();
	}

	protected void renderScene(Scene scene, boolean waterRenderingEnabled) {
		if (waterRenderingEnabled) {
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			renderWaterReflectionPass(scene);
			renderWaterRefractionPass(scene);
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);	
		}
		renderMainPass(scene);
	}

	private void renderMainPass(Scene scene) {
		prepare();
		skyRenderer.render(scene.getSky(), scene.getCamera());
		sunRenderer.render(scene.getSun(), scene.getCamera());
		if (scene.getLensFlare() != null) {
			scene.getLensFlare().render(scene.getCamera(), scene.getSun().getWorldPosition(scene.getCamera().getPosition()));			
		}
		entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), NO_CLIP);
		terrainRenderer.render(scene.getTerrain(), scene.getCamera(), scene.getLight(), new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		// List<ITerrain> terrains = new ArrayList<ITerrain>();
		// terrains.add(scene.getTerrain());
		// terrainRenderer.render(terrains, this.shadowMapRenderer.getToShadowMapSpaceMatrix());
		waterRenderer.render(scene.getWater(), scene.getCamera(), scene.getLightDirection());
		for (WaterTileVao water : scene.getWatersVao()) {
			waterRendererVao.render(water, scene.getCamera(), scene.getLight(), reflectionFbo.getColourBuffer(0), refractionFbo.getColourBuffer(0), refractionFbo.getDepthBuffer());
		}
		animModelRenderer.render(scene.getAnimatedPlayer(), scene.getCamera(), scene.getLightDirection());
		guiRenderer.render(scene.getGuiElements());
		renderShadowMap(scene.getAdditionalEntities(), scene.getCamera(), scene.getSun().getLight());
		TextMaster.render();
		ParticleMaster.renderParticles(scene.getCamera());
	}

	/**
	 * Prepare to render the current frame by clearing the framebuffer.
	 */
	private void prepare() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	/**
	 * Sets up an FBO for one of the extra render passes. The FBO is initialized
	 * with a texture colour attachment, and can be initialized with either a
	 * render buffer or texture attachment for the depth buffer.
	 * 
	 * @param width
	 *            - The width of the FBO in pixels.
	 * @param height
	 *            - The height of the FBO in pixels.
	 * @param useTextureForDepth
	 *            - Whether the depth buffer attachment should be a texture or a
	 *            render buffer.
	 * @return The completed FBO.
	 */
	private static Fbo createWaterFbo(int width, int height, boolean useTextureForDepth) {
		Attachment colourAttach = new TextureAttachment(GL11.GL_RGBA8);
		Attachment depthAttach;
		if (useTextureForDepth) {
			depthAttach = new TextureAttachment(GL14.GL_DEPTH_COMPONENT24);
		} else {
			depthAttach = new RenderBufferAttachment(GL14.GL_DEPTH_COMPONENT24);
		}
		return Fbo.newFbo(width, height).addColourAttachment(0, colourAttach).addDepthAttachment(depthAttach).init();
	}

	private void renderWaterReflectionPass(Scene scene) {
		waterFbos.bindReflectionFrameBuffer();
		reflectionFbo.bindForRender(1);
		prepare();
		scene.getCamera().reflect(scene.getWaterHeight());
		scene.getTerrain().render(scene.getCamera(), scene.getLight(), new Vector4f(0, 1, 0, -scene.getWaterHeight() + REFLECT_OFFSET));
		entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), new Vector4f(0,1,0,0.1f));
		skyRenderer.render(scene.getSky(), scene.getCamera());
		waterFbos.unbindCurrentFrameBuffer();
		reflectionFbo.unbindAfterRender();
		scene.getCamera().reflect(scene.getWaterHeight());
	}

	private void renderWaterRefractionPass(Scene scene) {
		waterFbos.bindRefractionFrameBuffer();
		refractionFbo.bindForRender(1);
		prepare();
		scene.getTerrain().render(scene.getCamera(), scene.getLight(), new Vector4f(0, 1, 0, -scene.getWaterHeight() + REFRACT_OFFSET));
		entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), new Vector4f(0,-1,0, 0));
		waterFbos.unbindCurrentFrameBuffer();
		refractionFbo.unbindAfterRender();
	}

	public void renderLowQualityScene(Scene scene, ICamera cubeMapCamera){
		prepare();
		entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), NO_CLIP);
		skyRenderer.render(scene.getSky(), cubeMapCamera);
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void renderShadowMap(List<Entity> entityList, ICamera camera, Light sun) {
		for (Entity entity : entityList) {
			processEntity(entity);
		}
		shadowMapRenderer.render(entities, camera, sun);
		entities.clear();
	}

	public MasterRenderer processEntity(Entity entity) {
		TexturedModel entityModel = entity.getTexModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
		return this;
	}
}
