package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import collision.AABB;
import collision.IntersectData;
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
import toolbox.MousePicker;

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
		TexturedModel lampModel = new TexturedModel(OBJLoader.loadOBJModel("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern_texture_atlas")).setNumberOfRows(2);
		TexturedModel fernModelAtlas = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), fernTextureAtlas);

		Entity tree1 = new Entity(treeModel, new Vector3f(10, terrain.getHeightOfTerrain(10, -10), -10), 0, 0, 0, 14);
		tree1.setAABB(new AABB(new Vector3f(8, terrain.getHeightOfTerrain(8, -12), -12), new Vector3f(12, terrain.getHeightOfTerrain(12, -8) + 35, -8)));
		Entity tree2 = new Entity(treeModel, new Vector3f(100, terrain.getHeightOfTerrain(100, -70), -70), 0, 0, 0, 12);
		tree2.setAABB(new AABB(new Vector3f(98, terrain.getHeightOfTerrain(98, -72), -72), new Vector3f(102, terrain.getHeightOfTerrain(102, -68) + 30, -68)));
		Entity tree3 = new Entity(treeModel, new Vector3f(25, terrain.getHeightOfTerrain(25, -70), -70), 0, 0, 0, 12);
		tree3.setAABB(new AABB(new Vector3f(23, terrain.getHeightOfTerrain(23, -72), -72), new Vector3f(27, terrain.getHeightOfTerrain(27, -68) + 30, -68)));

		Entity tree4 = new Entity(lowPolyTreeModel, new Vector3f(100, terrain.getHeightOfTerrain(100, -250) - 10, -250), 0, 0, 0, 10);

		Entity box1 = new Entity(boxModel, new Vector3f(-360, terrain.getHeightOfTerrain(-360, 350) + 20, 350), 0, 0, 0, 20);
		box1.setAABB(new AABB(new Vector3f(-380, -10, 330), new Vector3f(-340, 42, 370)));
		box1.increaseRotation(0, -25f, 0);
		Entity box2 = new Entity(boxModel, new Vector3f(100, terrain.getHeightOfTerrain(100, 200) + 8, 200), 0, 0, 0, 10);
		box2.setAABB(new AABB(new Vector3f(90, -10, 190), new Vector3f(110, 22, 210)));
		Entity box3 = new Entity(boxModel, new Vector3f(-100, terrain.getHeightOfTerrain(-100, 120) + 30, 120), 0, 0, 0, 35);
		box3.setAABB(new AABB(new Vector3f(-135, -5, 85), new Vector3f(-65, 58, 155)));
		Entity box4 = new Entity(boxModel, new Vector3f(-100, terrain.getHeightOfTerrain(-100, 120) + 113, 120), 0, 0, 0, 30);
		box4.setAABB(new AABB(new Vector3f(-131, 80, 89), new Vector3f(-69, 143, 151)));
		Entity box5 = new Entity(boxModel, new Vector3f(-100, terrain.getHeightOfTerrain(-100, 120) + 185, 120), 0, 0, 0, 25);
		box5.setAABB(new AABB(new Vector3f(-124, 160, 96), new Vector3f(-76, 205, 144)));

		Entity pineTree1 = new Entity(pineTreeModel, new Vector3f(200, terrain.getHeightOfTerrain(200, -300) - 2, -300), 0, 0, 0, 5);
		Entity pineTree2 = new Entity(pineTreeModel, new Vector3f(240, terrain.getHeightOfTerrain(240, -260) - 2, -260), 0, 0, 0, 5);
		Entity pineTree3 = new Entity(pineTreeModel, new Vector3f(280, terrain.getHeightOfTerrain(280, -260) - 2, -260), 0, 0, 0, 5);
		Entity pineTree4 = new Entity(pineTreeModel, new Vector3f(320, terrain.getHeightOfTerrain(320, -280) - 2, -280), 0, 0, 0, 5);

		Entity stall1 = new Entity(stallModel, new Vector3f(260, terrain.getHeightOfTerrain(260, 280), 280), 0, 0, 0, 3);
		stall1.increaseRotation(0, 50f, 0);
		Entity stall2 = new Entity(stallModel, new Vector3f(-70, terrain.getHeightOfTerrain(-70, -190), -190), 0, 0, 0, 3);
		stall2.increaseRotation(0, -120, 0);

		Entity fern = new Entity(fernModel, new Vector3f(25, terrain.getHeightOfTerrain(25, 70), 70), 0, 0, 0, 2);
		fern.getModel().getTexture().setHasTransparency(true).setUseFakeLighting(true);
		
		Entity fern1 = new Entity(fernModelAtlas, 0, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 0), 0), 0, 0, 0, 3);
		Entity fern2 = new Entity(fernModelAtlas, 1, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 50), 50), 0, 0, 0, 3);
		Entity fern3 = new Entity(fernModelAtlas, 2, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 100), 100), 0, 0, 0, 3);
		Entity fern4 = new Entity(fernModelAtlas, 3, new Vector3f(-220, terrain.getHeightOfTerrain(-220, 150), 150), 0, 0, 0, 3);

		Entity bunny = new Entity(bunnyModel, new Vector3f(25, terrain.getHeightOfTerrain(25, -20), -20), 0, 0, 0, 0.5f);
		bunny.getModel().getTexture().setShineDamper(50).setReflectivity(50);

		// lights
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(1000, 10000, -7000), new Vector3f(1f, 1f, 1f))); // world light (sun)
		Entity lamp1 = new Entity(lampModel, new Vector3f(270, terrain.getHeightOfTerrain(270, -143) - 0.5f, -143), 0, 0, 0, 2);
		Light light1 = new Light(new Vector3f(270, terrain.getHeightOfTerrain(270, -143) + 20, -143), new Vector3f(2f, 2f, 4f), new Vector3f(1f, 0.01f, 0.001f)); // blue
		lights.add(light1);
		Entity lamp2 = new Entity(lampModel, new Vector3f(75, terrain.getHeightOfTerrain(75, -30), -30), 0, 0, 0, 2);
		lights.add(new Light(new Vector3f(75, terrain.getHeightOfTerrain(75, -30) + 20, -30), new Vector3f(2f, 0f, 0f), new Vector3f(1f, 0.01f, 0.0002f))); // red
		Entity lamp3 = new Entity(lampModel, new Vector3f(-75, terrain.getHeightOfTerrain(-75, -165), -165), 0, 0, 0, 2);
		lights.add(new Light(new Vector3f(-75, terrain.getHeightOfTerrain(-75, -165) + 20, -165), new Vector3f(2f, 2f, 0f), new Vector3f(1f, 0.01f, 0.001f))); // yellow
		Entity lamp4 = new Entity(lampModel, new Vector3f(155, terrain.getHeightOfTerrain(155, 120), 120), 0, 0, 0, 2);
		Light light4 = new Light(new Vector3f(155, terrain.getHeightOfTerrain(155, 120) + 20, 120), new Vector3f(2f, 2f, 0f), new Vector3f(1f, 0.01f, 0.001f)); // yellow 2 for lamp4
		lights.add(light4);

		Player player = new Player(playerModel, new Vector3f(0, 0, -100), 0, 0, 0, 1f);

		Camera camera = new Camera(player, terrain);

		MasterRenderer renderer = new MasterRenderer(loader);

		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(-0.75f, -0.8f), new Vector2f(0.2f, 0.2f));
		GuiTexture gui_health = new GuiTexture(loader.loadTexture("health"), new Vector2f(0.55f, -0.85f), new Vector2f(0.4f, 0.4f));
		guis.add(gui);
		guis.add(gui_health);
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		while(!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();

			// mouse picker (dragging entities around)
			if (picker.isDragEnabled()) {
				picker.update();
				// System.out.println(picker.getCurrentRay());
				Vector3f terrainPoint = picker.getCurrentTerrainPoint();
				if (terrainPoint != null) {
					lamp1.setPosition(terrainPoint);
					light1.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 20, terrainPoint.z));
				}	
			}

			// render
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processEntity(fern);
			renderer.processEntity(fern1).processEntity(fern2).processEntity(fern3).processEntity(fern4);
			renderer.processEntity(tree1).processEntity(tree2).processEntity(tree3);
			renderer.processEntity(tree4); // lowPolyTree
			renderer.processEntity(pineTree1).processEntity(pineTree2).processEntity(pineTree3).processEntity(pineTree4);
			renderer.processEntity(box1).processEntity(box2).processEntity(box3).processEntity(box4).processEntity(box5);
			renderer.processEntity(stall1).processEntity(stall2);
			renderer.processEntity(lamp1).processEntity(lamp2).processEntity(lamp3).processEntity(lamp4);

			renderer.render(lights, camera);
			guiRenderer.render(guis);

			if (player.getAABB().intersectAABB(box1.getAABB()).isIntersecting()) {
				if (player.getPosition().y <= 42) {
					player.getPosition().y = 42;
					player.setGravityEnabled(false);
				} else {
					player.setGravityEnabled(true);
				}
			} else if (player.getAABB().intersectAABB(box2.getAABB()).isIntersecting()) {
				if (player.getPosition().y <= 22) {
					player.getPosition().y = 22;
					player.setGravityEnabled(false);
				} else {
					player.setGravityEnabled(true);
				}
			} else if (player.getAABB().intersectAABB(box3.getAABB()).isIntersecting()) {
				if (player.getPosition().y <= 58) {
					player.getPosition().y = 58;
					player.setGravityEnabled(false);
				} else {
					player.setGravityEnabled(true);
				}
			} else if (player.getAABB().intersectAABB(box4.getAABB()).isIntersecting()) {
				if (player.getPosition().y <= 136.5f) {
					player.getPosition().y = 136.5f;
					player.setGravityEnabled(false);
				} else {
					player.setGravityEnabled(true);
				}
			} else if (player.getAABB().intersectAABB(box5.getAABB()).isIntersecting()) {
				if (player.getPosition().y <= 203) {
					player.getPosition().y = 203;
					player.setGravityEnabled(false);
				} else {
					player.setGravityEnabled(true);
				}
			} else {
				player.setGravityEnabled(true);
			}
			
			if (player.getAABB().intersectAABB(tree1.getAABB()).isIntersecting()) {
				tree1.increaseRotation(0, 2f, 0);
			}
			if (player.getAABB().intersectAABB(tree2.getAABB()).isIntersecting()) {
				tree2.increaseRotation(0, 2f, 0);
			}

			IntersectData intersectData = player.getAABB().intersectAABB(tree3.getAABB());
			if (intersectData.isIntersecting()) {
				tree3.increaseRotation(0, 2f, 0);
				// System.out.println("IntersectData:" + " isIntersecting = " 
				// + intersectData.isIntersecting() + " maxDistance = " + intersectData.getMaxDistance());
			}

			DisplayManager.updateDisplay();
		}
	
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
