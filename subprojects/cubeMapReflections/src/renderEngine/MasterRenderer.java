package renderEngine;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Entity;
import entityRenderer.EntityRenderer;
import environmentMapRenderer.CubeMap;
import environmentMapRenderer.SkyboxRenderer;

public class MasterRenderer {
	
	private static final String[] ENVIRO_MAP_SNOW = {"cposx", "cnegx", "cposy", "cnegy", "cposz", "cnegz"};
	private static final String[] ENVIRO_MAP_LAKE = {"posx", "negx", "posy", "negy", "posz", "negz"};
	private static final String[] ENVIRO_MAP_INSIDE = {"lposx", "lnegx", "lposy", "lnegy", "lposz", "lnegz"};
	//private static final String[] ENVIRO_MAP_3 = {"cposx", "cnegx", "cposy", "cnegy", "cposz", "cnegz"};
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;
	
	private SkyboxRenderer environmentRenderer;
	private EntityRenderer entityRenderer;
	
	public MasterRenderer(Loader loader){
		this.projectionMatrix = createProjectionMatrix();
		CubeMap enviroMap = new CubeMap(ENVIRO_MAP_INSIDE, loader);
		this.environmentRenderer = new SkyboxRenderer(enviroMap, projectionMatrix);
		this.entityRenderer = new EntityRenderer(projectionMatrix, enviroMap);
	}
	
	public void renderScene(List<Entity> entities, Camera camera){
		prepare();
		entityRenderer.render(entities, camera);
		environmentRenderer.render(camera);
	}
	
	public void cleanUp(){
		environmentRenderer.cleanUp();
		entityRenderer.cleanUp();
	}
	
	private static Matrix4f createProjectionMatrix(){
		Matrix4f projectionMatrix = new Matrix4f();
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
		return projectionMatrix;
	}
	
	private static void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

}
