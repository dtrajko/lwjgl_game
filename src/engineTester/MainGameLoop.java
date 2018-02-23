package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import collision.AABB;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import input.InputHelper;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystemComplex;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.Game;
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

		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>(); // entities using normal map rendering
		List<Terrain> terrains = new ArrayList<Terrain>();
		List<Light> lights = new ArrayList<Light>();
		List<WaterTile> waters = new ArrayList<WaterTile>();
		List<GuiTexture> guis = new ArrayList<GuiTexture>();

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		
		FontType font2 = new FontType(loader.loadTexture("candara", 0), "candara");
		String sampleText = "A sample string of text!";
		GUIText text2 = new GUIText(sampleText, 2.0f, font2, new Vector2f(0.02f, 0.02f), 0.5f, false);
		text2.setColour(1.0f, 0.4f, 0.0f);
		TextMaster.loadText(text2);

		GUIText text_pause = new GUIText("PAUSED",
				3.0f, font2, new Vector2f(0.25f, 0.45f), 0.5f, true);
		text_pause.setColour(0.5f, 1.0f, 0.5f);

		/**************** BEGIN TERRAIN TEXTURE STUFF ****************/

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		/**************** END TERRAIN TEXTURE STUFF ****************/

		// terrains
		Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap, "heightmap_crater");

		// models
		RawModel treeModelRaw = OBJFileLoader.loadOBJ("tree", loader);
		RawModel pineTreeModelRaw = OBJFileLoader.loadOBJ("pine", loader);
		RawModel stallModelRaw = OBJFileLoader.loadOBJ("stall", loader);
		RawModel bobbleTreeModelRaw = OBJFileLoader.loadOBJ("bobbleTree", loader);
		RawModel steveModelRaw = OBJFileLoader.loadOBJ("steve", loader);
		// RawModel lowPolyTreeModelRaw = loader.loadToVAO(lowPolyTreeData.getVertices(), lowPolyTreeData.getTextureCoords(), lowPolyTreeData.getNormals(), lowPolyTreeData.getIndices());
		// RawModel donutModelRaw = loader.loadToVAO(donutData.getVertices(), donutData.getTextureCoords(), donutData.getNormals(), donutData.getIndices());
		// RawModel piperModelRaw = loader.loadToVAO(piperData.getVertices(), piperData.getTextureCoords(), piperData.getNormals(), piperData.getIndices());
		TexturedModel treeModel = new TexturedModel(treeModelRaw, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel pineTreeModel = new TexturedModel(pineTreeModelRaw, new ModelTexture(loader.loadTexture("pine")));
		TexturedModel stallModel = new TexturedModel(stallModelRaw, new ModelTexture(loader.loadTexture("stallTexture")));
		TexturedModel boxModel = new TexturedModel(OBJLoader.loadOBJ("box", loader), new ModelTexture(loader.loadTexture("box")));
		TexturedModel lampModel = new TexturedModel(OBJLoader.loadOBJ("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));
		TexturedModel bobbleTreeModel = new TexturedModel(bobbleTreeModelRaw, new ModelTexture(loader.loadTexture("bobbleTree")));
		TexturedModel steveModel = new TexturedModel(steveModelRaw, new ModelTexture(loader.loadTexture("steve")));
		// TexturedModel lowPolyTreeModel = new TexturedModel(lowPolyTreeModelRaw, new ModelTexture(loader.loadTexture("lowPolyTree")));
		// TexturedModel bunnyModel = new TexturedModel(OBJLoader.loadOBJModel("bunny", loader), new ModelTexture(loader.loadTexture("fur")));
		// TexturedModel playerModel = new TexturedModel(OBJLoader.loadOBJModel("person", loader), new ModelTexture(loader.loadTexture("playerTexture")));
		// TexturedModel donutModel = new TexturedModel(donutModelRaw, new ModelTexture(loader.loadTexture("donut")));
		// TexturedModel piperModel = new TexturedModel(piperModelRaw, new ModelTexture(loader.loadTexture("piper_pa18")));
		// TexturedModel dragonModel = new TexturedModel(OBJLoader.loadOBJModel("dragon", loader), new ModelTexture(loader.loadTexture("dragon")));
		// TexturedModel blueDevil = new TexturedModel(OBJLoader.loadOBJModel("bluedevil", loader), new ModelTexture(loader.loadTexture("bluedevil")));
		// TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern_texture_atlas")).setNumberOfRows(2);
		TexturedModel fernModelAtlas = new TexturedModel(OBJLoader.loadOBJ("fern", loader), fernTextureAtlas);

		TexturedModel cherryModel = new TexturedModel(OBJFileLoader.loadOBJ("cherry", loader), 
				new ModelTexture(loader.loadTexture("cherry")));
		cherryModel.getTexture().setHasTransparency(true);
		cherryModel.getTexture().setShineDamper(10);
		cherryModel.getTexture().setReflectivity(0.5f);
		cherryModel.getTexture().setExtraInfoMap(loader.loadTexture("cherryS"));

		TexturedModel lanternModel = new TexturedModel(OBJFileLoader.loadOBJ("lantern", loader), 
				new ModelTexture(loader.loadTexture("lantern")));
		lanternModel.getTexture().setExtraInfoMap(loader.loadTexture("lanternS"));

		TexturedModel houseModel = new TexturedModel(OBJFileLoader.loadOBJ("res/socuwan scene/Houses/model", loader), 
				new ModelTexture(loader.loadTexture("res/socuwan scene/Houses/diffuse")));

		int tree1_x = -722;
		int tree1_z = -622;
		Entity tree1 = new Entity(treeModel, new Vector3f(tree1_x, terrain.getHeightOfTerrain(tree1_x, tree1_z), tree1_z), 0, 0, 0, 14);
		tree1.setAABB(new AABB(
				new Vector3f(tree1_x - 2, terrain.getHeightOfTerrain(tree1_x - 2, tree1_z - 2), tree1_z - 2), 
				new Vector3f(tree1_x + 2, terrain.getHeightOfTerrain(tree1_x + 2, tree1_z + 2) + 35, tree1_z + 2)));

		int tree2_x = -100;
		int tree2_z = -70;
		Entity tree2 = new Entity(treeModel, new Vector3f(tree2_x, terrain.getHeightOfTerrain(tree2_x, tree2_z), tree2_z), 0, 0, 0, 12);
		tree2.setAABB(new AABB(
				new Vector3f(tree2_x - 2, terrain.getHeightOfTerrain(tree2_x - 2, tree2_z - 2), tree2_z - 2), 
				new Vector3f(tree2_x + 2, terrain.getHeightOfTerrain(tree2_x + 2, tree2_z + 2) + 35, tree2_z + 2)));

		int tree3_x = -170;
		int tree3_z = -340;
		Entity tree3 = new Entity(treeModel, new Vector3f(tree3_x, terrain.getHeightOfTerrain(tree3_x, tree3_z), tree3_z), 0, 0, 0, 12);
		tree3.setAABB(new AABB(
				new Vector3f(tree3_x - 2, terrain.getHeightOfTerrain(tree3_x - 2, tree3_z - 2), tree3_z - 2), 
				new Vector3f(tree3_x + 2, terrain.getHeightOfTerrain(tree3_x + 2, tree3_z + 2) + 35, tree3_z + 2)));

		Entity bobbleTree = new Entity(bobbleTreeModel, new Vector3f(-50, terrain.getHeightOfTerrain(-50, -750), -750), 0, 0, 0, 2);
				
		Entity box1 = new Entity(boxModel, new Vector3f(-360, terrain.getHeightOfTerrain(-360, -350) + 20, -350), 0, 0, 0, 20);
		box1.setAABB(new AABB(new Vector3f(-380, -10, -330), new Vector3f(-340, 42, -370)));
		box1.increaseRotation(0, -25f, 0);

		Entity box2 = new Entity(boxModel, new Vector3f(-100, terrain.getHeightOfTerrain(-100, -200) + 8, -200), 0, 0, 0, 10);
		box2.setAABB(new AABB(new Vector3f(-90, -10, -190), new Vector3f(-110, 22, -210)));

		int box3XL_x = -688;
		int box3XL_z = -136;
		int box3XL_y = (int) terrain.getHeightOfTerrain(box3XL_x, box3XL_z);
		Entity box3XL = new Entity(boxModel, new Vector3f(box3XL_x, box3XL_y + 50, box3XL_z), 0, 0, 0, 25);
		box3XL.setAABB(new AABB(
				new Vector3f(box3XL_x - 30, box3XL_y + 30, box3XL_z - 30), 
				new Vector3f(box3XL_x + 30, box3XL_y + 80, box3XL_z + 30)));

		Entity pineTree1 = new Entity(pineTreeModel, new Vector3f(-300, terrain.getHeightOfTerrain(-300, -500) - 2, -500), 0, 0, 0, 5);
		Entity pineTree2 = new Entity(pineTreeModel, new Vector3f(-255, terrain.getHeightOfTerrain(-255, -720) - 2, -720), 0, 0, 0, 5); 
		Entity pineTree3 = new Entity(pineTreeModel, new Vector3f(-500, terrain.getHeightOfTerrain(-500, -490) - 2, -490), 0, 0, 0, 5);
		Entity pineTree4 = new Entity(pineTreeModel, new Vector3f(-420, terrain.getHeightOfTerrain(-420, -480) - 2, -480), 0, 0, 0, 5);
		Entity pineTree5 = new Entity(pineTreeModel, new Vector3f(-480, terrain.getHeightOfTerrain(-480, -560) - 2, -560), 0, 0, 0, 5);
		Entity pineTree6 = new Entity(pineTreeModel, new Vector3f(-550, terrain.getHeightOfTerrain(-550, -610) - 2, -610), 0, 0, 0, 5);
		Entity pineTree7 = new Entity(pineTreeModel, new Vector3f(-550, terrain.getHeightOfTerrain(-550, -660) - 2, -660), 0, 0, 0, 5);

		Entity stall1 = new Entity(stallModel, new Vector3f(-660, terrain.getHeightOfTerrain(-660, -280), -280), 0, 0, 0, 3);
		stall1.increaseRotation(0, 50f, 0);
		Entity stall2 = new Entity(stallModel, new Vector3f(-770, terrain.getHeightOfTerrain(-770, -190), -190), 0, 0, 0, 3);
		stall2.increaseRotation(0, -120, 0);

		Entity fern = new Entity(fernModelAtlas, 2, new Vector3f(-25, terrain.getHeightOfTerrain(-25, -70), -70), 0, 0, 0, 6);
		fern.getModel().getTexture().setHasTransparency(true).setUseFakeLighting(true);

		Entity fern1 = new Entity(fernModelAtlas, 0, new Vector3f(-235, terrain.getHeightOfTerrain(-235, -75), -75), 0, 0, 0, 3);
		Entity fern2 = new Entity(fernModelAtlas, 1, new Vector3f(-225, terrain.getHeightOfTerrain(-225, -175), -175), 0, 0, 0, 3);
		Entity fern3 = new Entity(fernModelAtlas, 2, new Vector3f(-225, terrain.getHeightOfTerrain(-225, -45), -45), 0, 0, 0, 3);
		Entity fern4 = new Entity(fernModelAtlas, 3, new Vector3f(-160, terrain.getHeightOfTerrain(-160, -195), -195), 0, 0, 0, 3);

		// normal map entities
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		barrelModel.getTexture().setHasTransparency(true);
		barrelModel.getTexture().setExtraInfoMap(loader.loadTexture("barrelS"));

		int barrel_x = -685;
		int barrel_z = -600;
		int barrel_y = (int) terrain.getHeightOfTerrain(barrel_x, barrel_z) + 25;
		Entity barrel = new Entity(barrelModel, new Vector3f(barrel_x, barrel_y, barrel_z), 0, 0, 0, 5f);
		barrel.setAABB(new AABB(
			new Vector3f(barrel_x - 15, barrel_y - 30, barrel_z - 15), 
			new Vector3f(barrel_x + 15, barrel_y + 40, barrel_z + 15)));

		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);
		Entity crate = new Entity(crateModel, new Vector3f(-545, 25, -562), 0, 0, 0, 0.1f);

		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);
		Entity boulder = new Entity(boulderModel, new Vector3f(-536, 40, -402), 0, 0, 0, 2.0f);

		Entity cherry = new Entity(cherryModel, new Vector3f(-200, terrain.getHeightOfTerrain(-200, -200), -200), 0, 0, 0, 5);
		Entity lantern = new Entity(lanternModel, new Vector3f(-140, terrain.getHeightOfTerrain(-140, -450), -450), 0, 0, 0, 1.5f);
		Entity house = new Entity(houseModel, new Vector3f(-390, terrain.getHeightOfTerrain(-390, -600) - 0, -600), 0, 0, 0, 14f);

		// player
		Player player = new Player(steveModel, new Vector3f(-40, terrain.getHeightOfTerrain(-40, -145) + 2, -145), 0, -70, 0, 4f);

		// camera
		Camera camera = new Camera(player, terrain);

		MasterRenderer renderer = new MasterRenderer(loader, camera);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());

		// lights
		Light sun = new Light(new Vector3f(15000, 10000, 15000), new Vector3f(1f, 1f, 1f)); // world light (sun)
		lights.add(sun);
		Entity lamp1 = new Entity(lampModel, new Vector3f(-270, terrain.getHeightOfTerrain(-270, -143) - 0.5f, -143), 0, 0, 0, 2);
		Light light1 = new Light(new Vector3f(-270, terrain.getHeightOfTerrain(-270, -143) + 20, -143), new Vector3f(2f, 2f, 4f), new Vector3f(1f, 0.01f, 0.001f)); // blue
		lights.add(light1);
		Entity lamp2 = new Entity(lampModel, new Vector3f(-75, terrain.getHeightOfTerrain(-75, -30), -30), 0, 0, 0, 2);
		Entity lamp3 = new Entity(lampModel, new Vector3f(-75, terrain.getHeightOfTerrain(-75, -165), -165), 0, 0, 0, 2);
		lights.add(new Light(new Vector3f(-75, terrain.getHeightOfTerrain(-75, -165) + 20, -165), new Vector3f(2f, 2f, 0f), new Vector3f(1f, 0.01f, 0.001f))); // yellow
		Entity lamp4 = new Entity(lampModel, new Vector3f(-180, terrain.getHeightOfTerrain(-180, -24), -24), 0, 0, 0, 2);
		Light light4 = new Light(new Vector3f(-180, terrain.getHeightOfTerrain(-180, -24) + 20, -24), new Vector3f(2f, 2f, 0f), new Vector3f(1f, 0.01f, 0.001f)); // yellow 2 for lamp4
		lights.add(light4);

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
		// entities.add(box4);
		// entities.add(box5);
		entities.add(stall1);
		entities.add(stall2);
		entities.add(lamp1);
		entities.add(lamp2);
		entities.add(lamp3);
		entities.add(lamp4);
		// entities.add(donut);
		entities.add(cherry);
		entities.add(lantern);
		entities.add(house);

		normalMapEntities.add(barrel);
		normalMapEntities.add(crate);
		normalMapEntities.add(boulder);

		GuiTexture gui_logo = new GuiTexture(loader.loadTexture("PlayStationLogo"), new Vector2f(-0.65f, -0.9f), new Vector2f(0.3f, 0.3f));
		GuiTexture gui_health = new GuiTexture(loader.loadTexture("health"), new Vector2f(0.65f, -0.85f), new Vector2f(0.3f, 0.3f));
		guis.add(gui_logo);
		guis.add(gui_health);

		// GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), 
		//     new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		// guis.add(shadowMap);

		GuiRenderer guiRenderer = new GuiRenderer(loader);

		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		// water
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader  waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		WaterTile water = new WaterTile(-400, -400, -5f);
		waters.add(water);
		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
		ParticleTexture particleTextureFire = new ParticleTexture(loader.loadTexture("fire"), 8, true);
		ParticleTexture particleTextureSmoke = new ParticleTexture(loader.loadTexture("smoke"), 8, false);
		
		// ParticleSystemSimple particleSystem = new ParticleSystemSimple(particleTexture, 40f, 10f, 0.1f, 1f);
		ParticleSystemComplex particleSystem = new ParticleSystemComplex(particleTexture,
			/* pps */ 50f, /* speed */ 0.5f, /* gravity */ -0.05f, /* life */ 20f, /* scale */ 2f);
		particleSystem.setLifeError(0.1f);
		particleSystem.setSpeedError(0.25f);
		particleSystem.setScaleError(0.5f);
		particleSystem.randomizeRotation();
		
		ParticleSystemComplex particleSystemFire = new ParticleSystemComplex(particleTextureFire,
			100f, 1f, -0.01f, 2f, 10f);
		particleSystemFire.setLifeError(0.1f);
		particleSystemFire.setSpeedError(0.25f);
		particleSystemFire.setScaleError(0.5f);
		particleSystemFire.randomizeRotation();
		ParticleSystemComplex particleSystemSmoke = new ParticleSystemComplex(particleTextureSmoke,
			100f, 1f, -0.05f, 20f, 5f);
		particleSystemSmoke.setLifeError(0.5f);
		particleSystemSmoke.setSpeedError(0.5f);
		particleSystemSmoke.setScaleError(0.5f);
		particleSystemSmoke.randomizeRotation();
		particleSystemSmoke.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f), 1f);

		Light fireLight = new Light(new Vector3f(-43, terrain.getHeightOfTerrain(-43, -56) - 2, -56), new Vector3f(1f, 1f, 1f), new Vector3f(1f, 0.01f, 0.0002f));
		lights.add(fireLight);

		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);

		DisplayManager.startFPS();

		while(!Display.isCloseRequested()) {

			DisplayManager.updateFPS();

			DisplayManager.switchDisplayMode();

			InputHelper.update();
			Game.checkIfRunning();

			if (Game.isRunning()) {

				player.move(terrain);
				camera.move();

				// TextMaster.removeText(text2);
				text2.setTextString(sampleText + " | FPS: " + DisplayManager.getFPS());
				TextMaster.loadText(text2);

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

				if (Mouse.isButtonDown(2)) { // 2 for mouse wheel button
					// new Particle(new Vector3f(player.getPosition()), new Vector3f(0, 30, 0), 1, 4, 0, 1);
					// particleSystemSimple.generateParticles(player.getPosition());
					particleSystem.generateParticles(new Vector3f(
							player.getPosition().getX(),
							player.getPosition().getY() + 12,
							player.getPosition().getZ()
					));
					// particleSystem.setDirection(new Vector3f(0.5f, 0.5f, 0.5f), 0f);
				}

				particleSystemFire.generateParticles(new Vector3f(-67, terrain.getHeightOfTerrain(-67, -120), -120));
				particleSystemSmoke.generateParticles(new Vector3f(-67, terrain.getHeightOfTerrain(-67, -120) + 1, -120));

				ParticleMaster.update(camera);

				renderer.renderShadowMap(normalMapEntities, sun);
				renderer.renderShadowMap(entities, sun);

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

				multisampleFbo.bindFrameBuffer();
				renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));
				waterRenderer.render(waters, camera, sun);
				ParticleMaster.renderParticles(camera);
				multisampleFbo.unbindFrameBuffer();
				multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
				multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
				// multisampleFbo.resolveToScreen();
				PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());

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
					if (player.getPosition().y <= box3XL_y + 75) {
						player.getPosition().y = box3XL_y + 75;
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
					barrel.increaseRotation(0, 0.5f, 0);
					if (player.getPosition().y <= barrel_y + 30f) {
						player.getPosition().y = barrel_y + 30f;
						player.setGravityEnabled(false);
					} else {
						player.setGravityEnabled(true);
					}
				}
			}

			TextMaster.render();
			DisplayManager.updateDisplay();
		}

		PostProcessing.cleanUp();
		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		multisampleFbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
