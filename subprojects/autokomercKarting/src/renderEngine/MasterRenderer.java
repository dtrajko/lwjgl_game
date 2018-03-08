package renderEngine;

import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import scene.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import environmentMapRenderer.CubeMapCamera;

public class MasterRenderer {

	public static final float FOV = 70; // field of view angle
	public static final float NEAR_PLANE = 1.0f;
	public static final float FAR_PLANE = 3000;

	public static final float RED = 1f;
	public static final float GREEN = 1f;
	public static final float BLUE = 1f;

	private Matrix4f projectionMatrix;

	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();

	private NormalMappingRenderer normalMapRenderer;

	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();

	public MasterRenderer(Loader loader, Camera cam) {
		enableCulling();
		createProjectionMatrix();
		this.renderer = new EntityRenderer(shader, projectionMatrix);
		this.terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		this.skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		this.normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
		this.shadowMapRenderer = new ShadowMapMasterRenderer(cam);
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void renderScene(List<Entity> entities, List<Terrain> terrains, 
			List<Light> lights, Camera camera, Vector4f clipPlane) {
		for (Terrain terrain : terrains) {
			this.processTerrain(terrain);
		}
		for (Entity entity : entities) {
			this.processEntity(entity);
		}
		render(lights, camera, clipPlane);
	}

	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {

		prepare();

		shader.start();
		// shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		
		normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);

		terrainShader.start();
		// terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains, this.shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();

		skyboxRenderer.render(camera, RED, GREEN, BLUE);

		terrains.clear();
		entities.clear();
		normalMapEntities.clear();
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public MasterRenderer processTerrain(Terrain terrain) {
		terrains.add(terrain);
		return this;
	}

	public MasterRenderer processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
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

	public MasterRenderer processNormalMapEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
		return this;
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(RED, GREEN, BLUE, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getShadowMapTexture());
	}

	private void createProjectionMatrix() {
		projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);;
		projectionMatrix.m33 = 0;
	}

	public void renderShadowMap(List<Entity> entityList, Light sun) {
		for (Entity entity : entityList) {
			processEntity(entity);
		}
		shadowMapRenderer.render(entities, sun);
		entities.clear();
	}

	public int getShadowMapTexture() {
		return this.shadowMapRenderer.getShadowMap();
	}

	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
		shadowMapRenderer.cleanUp();
	}

	public void renderLowQualityScene(Scene scene, CubeMapCamera camera) {
		// TODO Auto-generated method stub
	}
}
