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

		Light light = new Light(new Vector3f(50, 100, 50), new Vector3f(1, 1, 1));
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();

		// terrains
		Terrain terrain = new Terrain(0, -0.5f, loader, new ModelTexture(loader.loadTexture("grass")));
		Terrain terrain2 = new Terrain(-1, -0.5f, loader, new ModelTexture(loader.loadTexture("grass")));

		// models
		TexturedModel bunnyModel = new TexturedModel(OBJLoader.loadOBJModel("bunny", loader), new ModelTexture(loader.loadTexture("fur")));
		Entity bunny = new Entity(bunnyModel, new Vector3f(-20, 0, -20), 0, 0, 0, 1);
		bunny.getModel().getTexture().setShineDamper(50);
		bunny.getModel().getTexture().setReflectivity(50);

		TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		Entity grass = new Entity(grassModel, new Vector3f(-20, 0, 0), 0, 0, 0, 1);
		grass.getModel().getTexture().setHasTransparency(true);
		grass.getModel().getTexture().setUseFakeLighting(true);

		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
		Entity fern = new Entity(fernModel, new Vector3f(-20, 0, 5), 0, 0, 0, 1);
		fern.getModel().getTexture().setHasTransparency(true);
		fern.getModel().getTexture().setUseFakeLighting(true);

		while(!Display.isCloseRequested()) {

			// update
			fern.increaseRotation(0, -1f, 0);
			camera.move();

			// render
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processEntity(bunny);
			renderer.processEntity(grass);
			renderer.processEntity(fern);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
	
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
