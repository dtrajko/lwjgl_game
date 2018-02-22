package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Camera camera = new Camera();
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer(loader);
		List<Entity> entities = new ArrayList<Entity>();
		
		entities.add(new Entity(loadModel("meta", loader), new Vector3f(4, 1, 0), 0, 0.5f));
		entities.add(new Entity(loadModel("tea", loader), new Vector3f(0.3f, 1, 0), 0, 0.34f));
		entities.add(new Entity(loadModel("dragon", loader), new Vector3f(-4, 1, 0), 0, 0.3f));

		while(!Display.isCloseRequested()){
			camera.move();
			renderer.renderScene(entities, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}
	
	private static TexturedModel loadModel(String fileName, Loader loader){
		RawModel model = OBJFileLoader.loadOBJ(fileName, loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture(fileName));
		return new TexturedModel(model, texture);
	}


}
