package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		RawModel model = OBJLoader.loadOBJModel("dragon", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("gold")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(10);

		Entity entity = new Entity(staticModel, new Vector3f(0, 0, -20), 0, 0, 0, 1);

		Light light = new Light(new Vector3f(50, 100, 50), new Vector3f(1, 1, 1));
		
		Terrain terrain = new Terrain(0, -0.5f, loader, new ModelTexture(loader.loadTexture("golden_tiles")));
		Terrain terrain2 = new Terrain(-1, -0.5f, loader, new ModelTexture(loader.loadTexture("golden_tiles")));

		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();

		while(!Display.isCloseRequested()) {
			entity.increaseRotation(0, 0.5f, 0);
			camera.move();

			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processEntity(entity);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
	
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
