package renderEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import models.Mesh;
import shaders.StaticShader;
import toolbox.Maths;

public class Renderer {

	private static final float FOV = 40;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	private static final float WAVE_SPEED = 0.4f;

	private Matrix4f projectionMatrix;
	private StaticShader shader;
	private float time = 0;

	public Renderer() {
		this.shader = new StaticShader();
		createProjectionMatrix();
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.9f, 0.4f, 0.25f, 1);
	}

	public void render(Mesh mesh, Camera camera) {
		shader.start();
		updateTime();
		loadProjectionViewMatrix(camera);
		GL30.glBindVertexArray(mesh.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public void cleanUp() {
		shader.cleanUp();
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
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

	private void loadProjectionViewMatrix(Camera camera) {
		shader.loadCameraPosition(camera);
		Matrix4f viewMat = Maths.createViewMatrix(camera);
		Matrix4f projView = Matrix4f.mul(projectionMatrix, viewMat, null);
		shader.loadProjectionViewMatrix(projView);
	}
	
	private void updateTime(){
		time+=DisplayManager.getFrameTimeSeconds()*WAVE_SPEED;
		time %= 1;
		shader.loadTime(time);
	}

}
