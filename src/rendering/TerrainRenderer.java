package rendering;

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
import terrains.HeightMapTerrain;
import terrains.Terrain;
import textures.TerrainTexturePack;
import utils.Light;
import utils.Maths;
import utils.MyFile;

/**
 * A simple renderer that renders terrains.
 * 
 * @author Karl
 *
 */
public class TerrainRenderer implements ITerrainRenderer {

	private static final MyFile VERTEX_SHADER = new MyFile("rendering", "flatTerrainVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("rendering", "flatTerrainFragment.glsl");

	private final TerrainShader shader;
	private final boolean hasIndices;

	/**
	 * @param shader
	 *            - The shader program used for rendering this terrain.
	 * @param usesIndices
	 *            - Indicates whether the terrain will be rendered with an index
	 *            buffer or not.
	 */
	public TerrainRenderer(boolean usesIndices) {
		this.shader = new TerrainShader(VERTEX_SHADER, FRAGMENT_SHADER);
		this.hasIndices = usesIndices;
	}

	/**
	 * Renders a terrain to the screen. If the terrain has an index buffer the
	 * glDrawElements is used. Otherwise glDrawArrays is used.
	 * 
	 * @param terrain
	 *            - The terrain to be rendered.
	 * @param camera
	 *            - The camera being used for rendering the terrain.
	 * @param light
	 *            - The light being used to iluminate the terrain.
	 * 
	 * @param clipPlane
	 *            - The equation of the clipping plane to be used when rendering
	 *            the terrain. The clipping planes cut off anything in the scene
	 *            that is rendered outside of the plane.
	 */
	@Override
	public void render(ITerrain terrain, ICamera camera, Light light, Vector4f clipPlane) {
		prepare(terrain, camera, light, clipPlane);
		if (hasIndices) {
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		} else {
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, terrain.getVertexCount());
		}
		finish(terrain);
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

	/**
	 * Used when the program closes. Deletes the shader program.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

	/**
	 * Starts the shader program and loads up any necessary uniform variables.
	 * 
	 * @param terrain
	 *            - The terrain to be rendered.
	 * @param camera
	 *            - The camera being used to render the scene.
	 * @param light
	 *            - The light in the scene.
	 * @param clipPlane
	 *            - The equation of the clipping plane to be used when rendering
	 *            the terrain. The clipping planes cut off anything in the scene
	 *            that is rendered outside of the plane.
	 */
	private void prepare(ITerrain terrain, ICamera camera, Light light, Vector4f clipPlane) {
		terrain.getVao().bind();
		shader.start();
		shader.plane.loadVec4(clipPlane);
		shader.lightBias.loadVec2(light.getLightBias());
		shader.lightDirection.loadVec3(light.getDirection());
		shader.lightColour.loadVec3(light.getColour().getVector());
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
	}

	/**
	 * End the rendering process by unbinding the VAO and stopping the shader
	 * program.
	 * 
	 * @param terrain
	 */
	private void finish(ITerrain terrain) {
		terrain.getVao().unbind();
		shader.stop();
	}
}
