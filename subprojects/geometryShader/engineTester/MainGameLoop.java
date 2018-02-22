package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
		float[] vertices = {			
				0f,0f,0f,
				2.5f,0f,0f,
				-2.5f,0f,0f,
		};
		
		float[] baseColours = {			
				1f, 1f, 1f,
				1f, 1f, 0f,
				0f, 1f, 1f,
		};

		
		RawModel model = loader.loadToVAO(vertices, baseColours);	
		Entity entity = new Entity(model, new Vector3f(0,0,-5),0,0,0,1);
		
		Camera camera = new Camera();
		
		while(!Display.isCloseRequested()){
			entity.increaseRotation(0, 1, 0);
			camera.move();
			renderer.prepare();
			shader.start();
			shader.loadViewMatrix(camera);
			renderer.render(entity,shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}

		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
