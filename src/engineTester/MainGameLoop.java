package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		StaticShader shader = new StaticShader();

		// OpenGL expects vertices to be defined counter clockwise by default
		float[] vertices = {
			// Left bottom triangle
			-0.5f,  0.5f, 0f, // V0
			-0.5f, -0.5f, 0f, // V1
			 0.5f, -0.5f, 0f, // V2
			 0.5f,  0.5f, 0f, // V3
		};
		
		int[] indices = {
				0, 1, 3, // Top left triangle (V0, V1, V3)
				3, 1, 2, // Bottom right triangle (V3, V1, V2)
		};
		
		float[] textureCoords = {
				0, 0, // V0
				0, 1, // V1
				1, 1, // V2
				1, 0, // V3
		};

		RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tiles")));
		Entity entity = new Entity(staticModel, new Vector3f(-0.3f, 0, 0), 0, 0, 0, 1);
		while(!Display.isCloseRequested()) {
			// game logic
			renderer.prepare();
			shader.start();
			renderer.render(entity, shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
