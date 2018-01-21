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

		// terrains
		Terrain terrain = new Terrain(-0.5f, -0.5f, loader, texturePack, blendMap, "heightmap");

		// models
		ModelData treeData = OBJFileLoader.loadOBJ("tree");
		ModelData lowPolyTreeData1 = OBJFileLoader.loadOBJ("lowPolyTree");
		RawModel treeModelRaw = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		RawModel lowPolyTreeModelRaw = loader.loadToVAO(lowPolyTreeData1.getVertices(), lowPolyTreeData1.getTextureCoords(), lowPolyTreeData1.getNormals(), lowPolyTreeData1.getIndices());
		TexturedModel treeModel = new TexturedModel(treeModelRaw, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel lowPolyTreeModel = new TexturedModel(lowPolyTreeModelRaw, new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel bunnyModel = new TexturedModel(OBJLoader.loadOBJModel("bunny", loader), new ModelTexture(loader.loadTexture("fur")));
		TexturedModel dragonModel = new TexturedModel(OBJLoader.loadOBJModel("dragon", loader), new ModelTexture(loader.loadTexture("dragon")));
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadOBJModel("person", loader), new ModelTexture(loader.loadTexture("playerTexture")));
		TexturedModel blueDevil = new TexturedModel(OBJLoader.loadOBJModel("bluedevil", loader), new ModelTexture(loader.loadTexture("bluedevil")));
		TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
		TexturedModel boxModel = new TexturedModel(OBJLoader.loadOBJModel("box", loader), new ModelTexture(loader.loadTexture("box")));

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern_texture_atlas"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fernModelAtlas = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), fernTextureAtlas);

		Entity tree1 = new Entity(treeModel, new Vector3f(10, terrain.getHeightOfTerrain(10, -10), -10), 0, 0, 0, 14);
		Entity tree2 = new Entity(treeModel, new Vector3f(40, terrain.getHeightOfTerrain(40, -50), -50), 0, 0, 0, 12);
		Entity tree3 = new Entity(lowPolyTreeModel, new Vector3f(100, terrain.getHeightOfTerrain(100, -250) - 10, -250), 0, 0, 0, 10);
		Entity bunny = new Entity(bunnyModel, new Vector3f(25, terrain.getHeightOfTerrain(25, -20), -20), 0, 0, 0, 0.5f);
		bunny.getModel().getTexture().setShineDamper(50).setReflectivity(50);
		
		Entity fern = new Entity(fernModel, new Vector3f(-10, terrain.getHeightOfTerrain(-10, 5), 5), 0, 0, 0, 2);
		fern.getModel().getTexture().setHasTransparency(true).setUseFakeLighting(true);
		
		Entity fern1 = new Entity(fernModelAtlas, 0, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 0), 0), 0, 0, 0, 5);
		Entity fern2 = new Entity(fernModelAtlas, 1, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 50), 50), 0, 0, 0, 5);
		Entity fern3 = new Entity(fernModelAtlas, 2, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 100), 100), 0, 0, 0, 5);
		Entity fern4 = new Entity(fernModelAtlas, 3, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 150), 150), 0, 0, 0, 5);

		Entity box1 = new Entity(boxModel, new Vector3f(0, terrain.getHeightOfTerrain(0, 200) + 8, 200), 0, 0, 0, 10);
		Entity box2 = new Entity(boxModel, new Vector3f(100, terrain.getHeightOfTerrain(100, 50) + 8, 50), 0, 0, 0, 10);
		Entity box3 = new Entity(boxModel, new Vector3f(-100, terrain.getHeightOfTerrain(-100, 120) + 26, 120), 0, 0, 0, 30);
		
		Player player = new Player(blueDevil, new Vector3f(0, -20, -100), 0, 0, 0, 0.6f);

		Camera camera = new Camera(player, terrain);
		Light light = new Light(new Vector3f(1000, 3000, -800), new Vector3f(1, 1, 1));
		MasterRenderer renderer = new MasterRenderer();

		while(!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();

			// render
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processEntity(bunny);
			renderer.processEntity(fern);
			renderer.processEntity(fern1).processEntity(fern2).processEntity(fern3).processEntity(fern4);
			renderer.processEntity(tree1).processEntity(tree2).processEntity(tree3);
			renderer.processEntity(box1).processEntity(box2).processEntity(box3);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
	
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
