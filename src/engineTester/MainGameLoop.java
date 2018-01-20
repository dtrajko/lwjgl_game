package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		/**************** BEGIN TERRAIN TEXTURE STUFF ****************/
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		/**************** END TERRAIN TEXTURE STUFF ****************/

		// models
		ModelData treeData = OBJFileLoader.loadOBJ("tree");
		RawModel treeModelRaw = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		TexturedModel treeModel = new TexturedModel(treeModelRaw, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel bunnyModel = new TexturedModel(OBJLoader.loadOBJModel("bunny", loader), new ModelTexture(loader.loadTexture("fur")));
		TexturedModel dragonModel = new TexturedModel(OBJLoader.loadOBJModel("dragon", loader), new ModelTexture(loader.loadTexture("dragon")));
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadOBJModel("person", loader), new ModelTexture(loader.loadTexture("playerTexture")));
		TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
		Entity tree1 = new Entity(treeModel, new Vector3f(10, 0, -10), 0, 0, 0, 5);
		Entity tree2 = new Entity(treeModel, new Vector3f(40, 0, -50), 0, 0, 0, 5);
		Entity tree3 = new Entity(treeModel, new Vector3f(70, 0, -100), 0, 0, 0, 5);
		Entity bunny = new Entity(bunnyModel, new Vector3f(25, 0, -20), 0, 0, 0, 0.5f);
		bunny.getModel().getTexture().setShineDamper(50).setReflectivity(50);
		grassModel.getTexture().setHasTransparency(true).setUseFakeLighting(true);
		Entity grass = new Entity(grassModel, new Vector3f(-5, 0, 0), 0, 0, 0, 2);
		Entity grass2 = new Entity(grassModel, new Vector3f(15, 0, -15), 0, 0, 0, 2);
		Entity fern = new Entity(fernModel, new Vector3f(-10, 0, 5), 0, 0, 0, 2);
		fern.getModel().getTexture().setHasTransparency(true).setUseFakeLighting(true);
		
		Player player = new Player(playerModel, new Vector3f(0, 0, -50), 0, 0, 0, 1);
		player.increaseRotation(0, 0, 0);

		// terrains
		Terrain terrain = new Terrain(-1, -0.5f, loader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(0, -0.5f, loader, texturePack, blendMap);

		Camera camera = new Camera(player);
		Light light = new Light(new Vector3f(1000, 3000, -800), new Vector3f(1, 1, 1));
		MasterRenderer renderer = new MasterRenderer();

		while(!Display.isCloseRequested()) {

			// update
			// fern.increaseRotation(0, -1f, 0);
			camera.move();

			// player
			player.move();

			// render
			renderer.processTerrain(terrain).processTerrain(terrain2);
			renderer.processEntity(player);
			renderer.processEntity(bunny);
			renderer.processEntity(grass).processEntity(grass2);
			renderer.processEntity(fern);
			renderer.processEntity(tree1).processEntity(tree2).processEntity(tree3);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
	
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
