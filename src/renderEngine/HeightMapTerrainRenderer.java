package renderEngine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import interfaces.ICamera;
import interfaces.ITerrain;
import interfaces.ITerrainRenderer;
import models.RawModel;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.TerrainTexturePack;
import utils.Light;
import utils.Maths;
import utils.OpenGlUtils;

public class HeightMapTerrainRenderer implements ITerrainRenderer {

	private TerrainShader shader;

	public HeightMapTerrainRenderer() {
		this.shader = new TerrainShader();
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
	}

	@Override
	public void render(List<ITerrain> terrains, Matrix4f toShadowSpace) {
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		for (ITerrain terrain:terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
					GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}

	public void render(ITerrain terrain, ICamera camera, Light light, Vector4f clipPlane) {
		prepareRender(camera, light);
		prepareTerrain(terrain);
		loadModelMatrix(terrain);
		GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
				GL11.GL_UNSIGNED_INT, 0);
		unbindTexturedModel();
	}

	private void prepareRender(ICamera camera, Light light) {
		shader.start();
		shader.loadProjectionMatrix(camera.getProjectionMatrix());
		shader.loadViewMatrix(camera);
		
		List<Light> lights = new ArrayList<Light>();
		lights.add(light);
		shader.loadLights(lights);

		doRenderSettings();
	}

	private void doRenderSettings() {
		OpenGlUtils.enableDepthTesting(true);
		OpenGlUtils.antialias(false);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.enableAlphaBlending();
	}

	private void prepareTerrain(ITerrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrain);
		shader.loadShineVariables(1, 0);
	}
	
	private void bindTextures(ITerrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getRedTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getGreenTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBlueTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(ITerrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
			shader.loadTransformationMatrix(transformationMatrix);
	}

	/**
	 * Used when the program closes. Deletes the shader program.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}
}
