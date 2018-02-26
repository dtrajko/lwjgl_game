package engineTester;

import org.lwjgl.opengl.Display;

import entities.Camera;
import models.Mesh;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Renderer renderer = new Renderer();

		Mesh vao = Mesh.create(loader);
		
		Camera camera = new Camera();

		while(!Display.isCloseRequested()){
			camera.move();
			renderer.prepare();
			renderer.render(vao, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
