package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
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
		ModelData lowPolyTreeData = OBJFileLoader.loadOBJ("lowPolyTree");
		ModelData pineTreeData = OBJFileLoader.loadOBJ("pine");
		ModelData stallData = OBJFileLoader.loadOBJ("stall");
		RawModel treeModelRaw = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		RawModel lowPolyTreeModelRaw = loader.loadToVAO(lowPolyTreeData.getVertices(), lowPolyTreeData.getTextureCoords(), lowPolyTreeData.getNormals(), lowPolyTreeData.getIndices());
		RawModel pineTreeModelRaw = loader.loadToVAO(pineTreeData.getVertices(), pineTreeData.getTextureCoords(), pineTreeData.getNormals(), pineTreeData.getIndices());
		RawModel stallModelRaw = loader.loadToVAO(stallData.getVertices(), stallData.getTextureCoords(), stallData.getNormals(), stallData.getIndices());
		TexturedModel treeModel = new TexturedModel(treeModelRaw, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel lowPolyTreeModel = new TexturedModel(lowPolyTreeModelRaw, new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel pineTreeModel = new TexturedModel(pineTreeModelRaw, new ModelTexture(loader.loadTexture("pine")));
		TexturedModel stallModel = new TexturedModel(stallModelRaw, new ModelTexture(loader.loadTexture("stallTexture")));
		TexturedModel bunnyModel = new TexturedModel(OBJLoader.loadOBJModel("bunny", loader), new ModelTexture(loader.loadTexture("fur")));
		TexturedModel dragonModel = new TexturedModel(OBJLoader.loadOBJModel("dragon", loader), new ModelTexture(loader.loadTexture("dragon")));
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadOBJModel("person", loader), new ModelTexture(loader.loadTexture("playerTexture")));
		TexturedModel blueDevil = new TexturedModel(OBJLoader.loadOBJModel("bluedevil", loader), new ModelTexture(loader.loadTexture("bluedevil")));
		TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
		TexturedModel boxModel = new TexturedModel(OBJLoader.loadOBJModel("box", loader), new ModelTexture(loader.loadTexture("box")));

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern_texture_atlas")).setNumberOfRows(2);
		TexturedModel fernModelAtlas = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), fernTextureAtlas);

		Entity tree1 = new Entity(treeModel, new Vector3f(10, terrain.getHeightOfTerrain(10, -10), -10), 0, 0, 0, 14);
		Entity tree2 = new Entity(treeModel, new Vector3f(40, terrain.getHeightOfTerrain(40, -50), -50), 0, 0, 0, 12);
		Entity tree3 = new Entity(lowPolyTreeModel, new Vector3f(100, terrain.getHeightOfTerrain(100, -250) - 10, -250), 0, 0, 0, 10);
		Entity pineTree1 = new Entity(pineTreeModel, new Vector3f(200, terrain.getHeightOfTerrain(200, -300) - 2, -300), 0, 0, 0, 5);
		Entity pineTree2 = new Entity(pineTreeModel, new Vector3f(240, terrain.getHeightOfTerrain(240, -260) - 2, -260), 0, 0, 0, 5);
		Entity pineTree3 = new Entity(pineTreeModel, new Vector3f(280, terrain.getHeightOfTerrain(280, -260) - 2, -260), 0, 0, 0, 5);
		Entity pineTree4 = new Entity(pineTreeModel, new Vector3f(320, terrain.getHeightOfTerrain(320, -280) - 2, -280), 0, 0, 0, 5);

		Entity stall1 = new Entity(stallModel, new Vector3f(260, terrain.getHeightOfTerrain(260, 280), 280), 0, 0, 0, 3);
		stall1.increaseRotation(0, 50f, 0);
		Entity stall2 = new Entity(stallModel, new Vector3f(-70, terrain.getHeightOfTerrain(-70, -190), -190), 0, 0, 0, 3);
		stall2.increaseRotation(0, -120, 0);

		Entity fern = new Entity(fernModel, new Vector3f(-10, terrain.getHeightOfTerrain(-10, 5), 5), 0, 0, 0, 2);
		fern.getModel().getTexture().setHasTransparency(true).setUseFakeLighting(true);
		
		Entity fern1 = new Entity(fernModelAtlas, 0, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 0), 0), 0, 0, 0, 3);
		Entity fern2 = new Entity(fernModelAtlas, 1, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 50), 50), 0, 0, 0, 3);
		Entity fern3 = new Entity(fernModelAtlas, 2, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 100), 100), 0, 0, 0, 3);
		Entity fern4 = new Entity(fernModelAtlas, 3, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 150), 150), 0, 0, 0, 3);

		Entity bunny = new Entity(bunnyModel, new Vector3f(25, terrain.getHeightOfTerrain(25, -20), -20), 0, 0, 0, 0.5f);
		bunny.getModel().getTexture().setShineDamper(50).setReflectivity(50);

		Entity box1 = new Entity(boxModel, new Vector3f(-360, terrain.getHeightOfTerrain(-360, 350) + 20, 350), 0, 0, 0, 20);
		box1.increaseRotation(0, -25f, 0);
		Entity box2 = new Entity(boxModel, new Vector3f(100, terrain.getHeightOfTerrain(100, 200) + 8, 200), 0, 0, 0, 10);
		Entity box3 = new Entity(boxModel, new Vector3f(-100, terrain.getHeightOfTerrain(-100, 120) + 26, 120), 0, 0, 0, 30);

		Player player = new Player(playerModel, new Vector3f(0, 0, -100), 0, 0, 0, 1f);

		Camera camera = new Camera(player, terrain);

		Light light = new Light(new Vector3f(0, 10000, -7000), new Vector3f(1, 1, 1));
		List<Light> lights = new ArrayList<Light>();
		lights.add(light);
		lights.add(new Light(new Vector3f(-200, 10, -200), new Vector3f(1, 1, 1)));
		lights.add(new Light(new Vector3f(200, 10, 200), new Vector3f(0.8f, 1f, 1f)));

		MasterRenderer renderer = new MasterRenderer();

		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(-0.75f, -0.8f), new Vector2f(0.2f, 0.2f));
		GuiTexture gui_health = new GuiTexture(loader.loadTexture("health"), new Vector2f(0.55f, -0.85f), new Vector2f(0.4f, 0.4f));
		guis.add(gui);
		guis.add(gui_health);
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		while(!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();

			// render
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processEntity(fern);
			renderer.processEntity(fern1).processEntity(fern2).processEntity(fern3).processEntity(fern4);
			renderer.processEntity(tree1).processEntity(tree2).processEntity(tree3);
			renderer.processEntity(pineTree1).processEntity(pineTree2).processEntity(pineTree3).processEntity(pineTree4);
			renderer.processEntity(box1).processEntity(box2).processEntity(box3);
			renderer.processEntity(stall1).processEntity(stall2);

			renderer.render(lights, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
	
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
