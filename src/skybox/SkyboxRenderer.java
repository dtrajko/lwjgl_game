package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import interfaces.ICamera;
import openglObjects.Vao;
import utils.OpenGlUtils;

public class SkyboxRenderer {

	private static final float SIZE = 200;

	private SkyboxShader shader;
	private Vao box;

	public SkyboxRenderer() {
		this.shader = new SkyboxShader();
		this.box = CubeGenerator.generateCube(SIZE);
	}
	
	public void render(Skybox skybox, ICamera iCamera){
		prepare(skybox, iCamera);
		Vao model = skybox.getCubeVao();
		model.bind(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		model.unbind(0);
		finish();
	}

	/**
	 * Delete the shader when the game closes.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

	/**
	 * Starts the shader, loads the projection-view matrix to the uniform
	 * variable, and sets some OpenGL state which should be mostly
	 * self-explanatory.
	 * 
	 * @param camera
	 *            - the scene's camera.
	 */
	private void prepare(ICamera camera) {
		shader.start();
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.antialias(false);
	}

	private void prepare(Skybox skybox, ICamera camera){
		shader.start();
		GL11.glDepthMask(false);
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix().translate(new Vector3f(0f, -50f, 0f)));
		skybox.getTexture().bindToUnit(0);
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.antialias(false);
	}

	private void finish() {
		GL11.glDepthMask(true);
		shader.stop();
	}	
}
