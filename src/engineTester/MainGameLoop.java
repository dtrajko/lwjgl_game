package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import collision.AABB;
import collision.IntersectData;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import renderEngine.Game;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {

		Game game = new Game();

		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>(); // entities using normal map rendering
		List<Terrain> terrains = new ArrayList<Terrain>();
		List<Light> lights = new ArrayList<Light>();
		List<WaterTile> waters = new ArrayList<WaterTile>();
		List<GuiTexture> guis = new ArrayList<GuiTexture>();

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);

		FontType font = new FontType(loader.loadTexture("tahoma", 0), new File("res/tahoma.fnt"));
		GUIText text = new GUIText("Frankenstein's monster in Wonderland",
			1.5f, font, new Vector2f(-0.06f, 0.01f), 0.5f, true);
		text.setColour(0.2f, 0.4f, 0.8f);

		/**************** BEGIN TERRAIN TEXTURE STUFF ****************/

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		/**************** END TERRAIN TEXTURE STUFF ****************/

		// terrains
		Terrain terrain = new Terrain(-0.5f, -0.5f, loader, texturePack, blendMap, "heightmap_crater");

		// models
		ModelData treeData = OBJFileLoader.loadOBJ("tree");
		ModelData lowPolyTreeData = OBJFileLoader.loadOBJ("lowPolyTree");
		ModelData pineTreeData = OBJFileLoader.loadOBJ("pine");
		ModelData stallData = OBJFileLoader.loadOBJ("stall");
		ModelData bobbleTreeData = OBJFileLoader.loadOBJ("bobbleTree");
		RawModel treeModelRaw = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		RawModel lowPolyTreeModelRaw = loader.loadToVAO(lowPolyTreeData.getVertices(), lowPolyTreeData.getTextureCoords(), lowPolyTreeData.getNormals(), lowPolyTreeData.getIndices());
		RawModel pineTreeModelRaw = loader.loadToVAO(pineTreeData.getVertices(), pineTreeData.getTextureCoords(), pineTreeData.getNormals(), pineTreeData.getIndices());
		RawModel stallModelRaw = loader.loadToVAO(stallData.getVertices(), stallData.getTextureCoords(), stallData.getNormals(), stallData.getIndices());
		RawModel bobbleTreeModelRaw = loader.loadToVAO(bobbleTreeData.getVertices(), bobbleTreeData.getTextureCoords(), bobbleTreeData.getNormals(), bobbleTreeData.getIndices());
		TexturedModel treeModel = new TexturedModel(treeModelRaw, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel lowPolyTreeModel = new TexturedModel(lowPolyTreeModelRaw, new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel pineTreeModel = new TexturedModel(pineTreeModelRaw, new ModelTexture(loader.loadTexture("pine")));
		TexturedModel stallModel = new TexturedModel(stallModelRaw, new ModelTexture(loader.loadTexture("stallTexture")));
		TexturedModel bunnyModel = new TexturedModel(OBJLoader.loadOBJModel("bunny", loader), new ModelTexture(loader.loadTexture("fur")));
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadOBJModel("person", loader), new ModelTexture(loader.loadTexture("playerTexture")));
		TexturedModel boxModel = new TexturedModel(OBJLoader.loadOBJModel("box", loader), new ModelTexture(loader.loadTexture("box")));
		TexturedModel lampModel = new TexturedModel(OBJLoader.loadOBJModel("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));
		TexturedModel bobbleTreeModel = new TexturedModel(bobbleTreeModelRaw, new ModelTexture(loader.loadTexture("bobbleTree")));
		// TexturedModel dragonModel = new TexturedModel(OBJLoader.loadOBJModel("dragon", loader), new ModelTexture(loader.loadTexture("dragon")));
		// TexturedModel blueDevil = new TexturedModel(OBJLoader.loadOBJModel("bluedevil", loader), new ModelTexture(loader.loadTexture("bluedevil")));
		// TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern_texture_atlas")).setNumberOfRows(2);
		TexturedModel fernModelAtlas = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), fernTextureAtlas);

		Entity tree1 = new Entity(treeModel, new Vector3f(10, terrain.getHeightOfTerrain(10, -10), -10), 0, 0, 0, 14);
		tree1.setAABB(new AABB(new Vector3f(8, terrain.getHeightOfTerrain(8, -12), -12), new Vector3f(12, terrain.getHeightOfTerrain(12, -8) + 35, -8)));
		Entity tree2 = new Entity(treeModel, new Vector3f(100, terrain.getHeightOfTerrain(100, -70), -70), 0, 0, 0, 12);
		tree2.setAABB(new AABB(new Vector3f(98, terrain.getHeightOfTerrain(98, -72), -72), new Vector3f(102, terrain.getHeightOfTerrain(102, -68) + 30, -68)));
		Entity tree3 = new Entity(treeModel, new Vector3f(25, terrain.getHeightOfTerrain(25, -70), -70), 0, 0, 0, 12);
		tree3.setAABB(new AABB(new Vector3f(23, terrain.getHeightOfTerrain(23, -72), -72), new Vector3f(27, terrain.getHeightOfTerrain(27, -68) + 30, -68)));

		Entity bobbleTree = new Entity(bobbleTreeModel, new Vector3f(100, terrain.getHeightOfTerrain(100, -250), -250), 0, 0, 0, 2);

		Entity box1 = new Entity(boxModel, new Vector3f(-360, terrain.getHeightOfTerrain(-360, 350) + 20, 350), 0, 0, 0, 20);
		box1.setAABB(new AABB(new Vector3f(-380, -10, 330), new Vector3f(-340, 42, 370)));
		box1.increaseRotation(0, -25f, 0);
		Entity box2 = new Entity(boxModel, new Vector3f(100, terrain.getHeightOfTerrain(100, 200) + 8, 200), 0, 0, 0, 10);
		box2.setAABB(new AABB(new Vector3f(90, -10, 190), new Vector3f(110, 22, 210)));
		Entity box3XL = new Entity(boxModel, new Vector3f(350, terrain.getHeightOfTerrain(350, 165) + 50, 165), 0, 0, 0, 25);
		box3XL.setAABB(new AABB(new Vector3f(320, 20, 135), new Vector3f(380, 100, 195)));
		
		Entity box4 = new Entity(boxModel, new Vector3f(-100, terrain.getHeightOfTerrain(-100, 120) + 113, 120), 0, 0, 0, 30);
		box4.setAABB(new AABB(new Vector3f(-131, 80, 89), new Vector3f(-69, 143, 151)));
		Entity box5 = new Entity(boxModel, new Vector3f(-100, terrain.getHeightOfTerrain(-100, 120) + 185, 120), 0, 0, 0, 25);
		box5.setAABB(new AABB(new Vector3f(-124, 160, 96), new Vector3f(-76, 205, 144)));

		Entity pineTree1 = new Entity(pineTreeModel, new Vector3f(200, terrain.getHeightOfTerrain(200, -300) - 2, -300), 0, 0, 0, 5);
		Entity pineTree2 = new Entity(pineTreeModel, new Vector3f(240, terrain.getHeightOfTerrain(240, -260) - 2, -260), 0, 0, 0, 5);
		Entity pineTree3 = new Entity(pineTreeModel, new Vector3f(280, terrain.getHeightOfTerrain(280, -260) - 2, -260), 0, 0, 0, 5);
		Entity pineTree4 = new Entity(pineTreeModel, new Vector3f(320, terrain.getHeightOfTerrain(320, -280) - 2, -280), 0, 0, 0, 5);

		Entity pineTree5 = new Entity(pineTreeModel, new Vector3f(-240, terrain.getHeightOfTerrain(-240, -50) - 2, -50), 0, 0, 0, 5);
		Entity pineTree6 = new Entity(pineTreeModel, new Vector3f(-250, terrain.getHeightOfTerrain(-250, -110) - 2, -110), 0, 0, 0, 5);
		Entity pineTree7 = new Entity(pineTreeModel, new Vector3f(-250, terrain.getHeightOfTerrain(-250, -160) - 2, -160), 0, 0, 0, 5);

		Entity stall1 = new Entity(stallModel, new Vector3f(260, terrain.getHeightOfTerrain(260, 280), 280), 0, 0, 0, 3);
		stall1.increaseRotation(0, 50f, 0);
		Entity stall2 = new Entity(stallModel, new Vector3f(-70, terrain.getHeightOfTerrain(-70, -190), -190), 0, 0, 0, 3);
		stall2.increaseRotation(0, -120, 0);

		Entity fern = new Entity(fernModelAtlas, 2, new Vector3f(25, terrain.getHeightOfTerrain(25, 70), 70), 0, 0, 0, 6);
		fern.getModel().getTexture().setHasTransparency(true).setUseFakeLighting(true);

		Entity fern1 = new Entity(fernModelAtlas, 0, new Vector3f(-235, terrain.getHeightOfTerrain(-235, -75), -75), 0, 0, 0, 3);
		Entity fern2 = new Entity(fernModelAtlas, 1, new Vector3f(-225, terrain.getHeightOfTerrain(-225, -175), -175), 0, 0, 0, 3);
		Entity fern3 = new Entity(fernModelAtlas, 2, new Vector3f(-225, terrain.getHeightOfTerrain(-225, -45), -45), 0, 0, 0, 3);
		Entity fern4 = new Entity(fernModelAtlas, 3, new Vector3f(-160, terrain.getHeightOfTerrain(-160, -195), -195), 0, 0, 0, 3);

		Entity bunny = new Entity(bunnyModel, new Vector3f(25, terrain.getHeightOfTerrain(25, -20), -20), 0, 0, 0, 0.5f);
		bunny.getModel().getTexture().setShineDamper(50).setReflectivity(50);

		Player player = new Player(playerModel, new Vector3f(-40, terrain.getHeightOfTerrain(-40, -145), -145), 0, -70, 0, 1f);

		// lights
		Light sun = new Light(new Vector3f(1000, 10000, -7000), new Vector3f(1f, 1f, 1f)); // world light (sun)
		lights.add(sun);
		Entity lamp1 = new Entity(lampModel, new Vector3f(270, terrain.getHeightOfTerrain(270, -143) - 0.5f, -143), 0, 0, 0, 2);
		Light light1 = new Light(new Vector3f(270, terrain.getHeightOfTerrain(270, -143) + 20, -143), new Vector3f(2f, 2f, 4f), new Vector3f(1f, 0.01f, 0.001f)); // blue
		lights.add(light1);
		Entity lamp2 = new Entity(lampModel, new Vector3f(75, terrain.getHeightOfTerrain(75, -30), -30), 0, 0, 0, 2);
		lights.add(new Light(new Vector3f(75, terrain.getHeightOfTerrain(75, -30) + 20, -30), new Vector3f(2f, 0f, 0f), new Vector3f(1f, 0.01f, 0.0002f))); // red
		Entity lamp3 = new Entity(lampModel, new Vector3f(-75, terrain.getHeightOfTerrain(-75, -165), -165), 0, 0, 0, 2);
		lights.add(new Light(new Vector3f(-75, terrain.getHeightOfTerrain(-75, -165) + 20, -165), new Vector3f(2f, 2f, 0f), new Vector3f(1f, 0.01f, 0.001f))); // yellow
		Entity lamp4 = new Entity(lampModel, new Vector3f(-180, terrain.getHeightOfTerrain(-180, -24), -24), 0, 0, 0, 2);
		Light light4 = new Light(new Vector3f(-180, terrain.getHeightOfTerrain(-180, -24) + 20, -24), new Vector3f(2f, 2f, 0f), new Vector3f(1f, 0.01f, 0.001f)); // yellow 2 for lamp4
		lights.add(light4);

		// normal map entities
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		Entity barrel = new Entity(barrelModel, new Vector3f(-185, 10, -100), 0, 0, 0, 1f);
		barrel.setAABB(new AABB(new Vector3f(-187, 4, -102), new Vector3f(-183, 18, -98)));

		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);
		Entity crate = new Entity(crateModel, new Vector3f(-145, 25, 62), 0, 0, 0, 0.1f);

		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);
		Entity boulder = new Entity(boulderModel, new Vector3f(-136, 40, 202), 0, 0, 0, 2.0f);

		terrains.add(terrain);
		entities.add(player);
		entities.add(fern);
		entities.add(fern1);
		entities.add(fern2);
		entities.add(fern3);
		entities.add(fern4);
		entities.add(tree1);
		entities.add(tree2);
		entities.add(tree3);
		entities.add(bobbleTree); // bobbleTree
		entities.add(pineTree1);
		entities.add(pineTree2);
		entities.add(pineTree3);
		entities.add(pineTree4);
		entities.add(pineTree5); // by the water
		entities.add(pineTree6); // by the water
		entities.add(pineTree7); // by the water
		entities.add(box1);
		entities.add(box2);
		entities.add(box3XL);
		entities.add(box4);
		entities.add(box5);
		entities.add(stall1);
		entities.add(stall2);
		entities.add(lamp1);
		entities.add(lamp2);
		entities.add(lamp3);
		entities.add(lamp4);
		normalMapEntities.add(barrel);
		normalMapEntities.add(crate);
		normalMapEntities.add(boulder);

		Camera camera = new Camera(player, terrain);

		MasterRenderer renderer = new MasterRenderer(loader);

		GuiTexture gui_logo = new GuiTexture(loader.loadTexture("PlayStationLogo"), new Vector2f(-0.65f, -0.9f), new Vector2f(0.3f, 0.3f));
		GuiTexture gui_health = new GuiTexture(loader.loadTexture("health"), new Vector2f(0.65f, -0.85f), new Vector2f(0.3f, 0.3f));
		guis.add(gui_logo);
		guis.add(gui_health);
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		// water
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader  waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		WaterTile water = new WaterTile(-190, -105, -1f);
		waters.add(water);

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

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			// render reflection texture
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()));
			camera.getPosition().y += distance;
			camera.invertPitch();

			// render refraction texture
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
			
			// render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));

			waterRenderer.render(waters, camera, sun);
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
			} else if (player.getAABB().intersectAABB(box3XL.getAABB()).isIntersecting()) {
				if (player.getPosition().y <= 75) {
					player.getPosition().y = 75;
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
			if (player.getAABB().intersectAABB(tree3.getAABB()).isIntersecting()) {
				tree3.increaseRotation(0, 2f, 0);
			}
			
			if (player.getAABB().intersectAABB(barrel.getAABB()).isIntersecting()) {
				barrel.increaseRotation(0, 4f, 0);
				if (player.getPosition().y <= 16f) {
					player.getPosition().y = 16f;
					player.setGravityEnabled(false);
				} else {
					player.setGravityEnabled(true);
				}
			}

			TextMaster.render();

			DisplayManager.updateDisplay();
		}
	
		TextMaster.cleanUp();
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
