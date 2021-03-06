package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import interfaces.ICamera;
import openglObjects.Vao;
import utils.DisplayManager;
import utils.OpenGlUtils;

public class SkyboxRenderer {

	private static final float SIZE = 200;
	private static final float ROTATE_SPEED = 1f;

	private SkyboxShader shader;
	private Vao box;
	
	private float rotation = 0;

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
		
		Matrix4f projectionViewMatrix = camera.getProjectionViewMatrix();
		projectionViewMatrix.translate(new Vector3f(0f, -50f, 0f));
		rotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), projectionViewMatrix, projectionViewMatrix);
		shader.projectionViewMatrix.loadMatrix(projectionViewMatrix);
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
