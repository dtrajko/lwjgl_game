package loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import animatedModel.AnimatedModel;
import animation.Animation;
import entities.Entity;
import entities.Player;
import extra.Camera;
import generation.ColourGenerator;
import generation.PerlinNoise;
import hybridTerrain.HybridTerrainGenerator;
import lensFlare.FlareFactory;
import lensFlare.FlareManager;
import lensFlare.FlareTexture;
import main.GeneralSettings;
import main.WorldSettings;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.ParticleSystemComplex;
import particles.ParticleTexture;
import scene.SceneEntity;
import scene.Scene;
import skybox.Skybox;
import sunRenderer.Sun;
import sunRenderer.SunRenderer;
import terrains.Terrain;
import terrains.TerrainGenerator;
import textures.ModelTexture;
import textures.Texture;
import scene.ICamera;
import utils.Light;
import utils.MyFile;
import water.WaterGenerator;
import water.WaterTileAux;

public class SceneLoader {

	private EntityLoader entityLoader;
	private SkyboxLoader skyLoader;
	private static Scene scene;
	private Loader loader;

	public SceneLoader(EntityLoader entityLoader, SkyboxLoader skyLoader) {
		this.entityLoader = entityLoader;
		this.skyLoader = skyLoader;
	}

	public Scene loadScene(MyFile resFolder, MyFile sceneFile) {
		MyFile sceneList = new MyFile(sceneFile, LoaderSettings.ENTITY_LIST_FILE);
		BufferedReader reader = getReader(sceneList);
		MyFile[] terrainFiles = readEntityFiles(reader, sceneFile);
		MyFile[] shinyFiles = readEntityFiles(reader, sceneFile);
		MyFile[] entityFiles = readEntityFiles(reader, sceneFile);
		// disable all terrains and entities for the lensFlare project
		terrainFiles = new MyFile[0];
		shinyFiles = new MyFile[0];
		entityFiles = new MyFile[0];

		closeReader(reader);
		// Skybox sky = skyLoader.loadSkyBox(new MyFile(sceneFile, LoaderSettings.SKYBOX_FOLDER));
		Skybox sky = skyLoader.loadSkyBox(new MyFile(new MyFile("skybox"), LoaderSettings.SKYBOX_FOLDER_II));

		// initialize sun and lens flare and set sun direction
		Light light = new Light(WorldSettings.LIGHT_DIR, WorldSettings.LIGHT_COL, WorldSettings.LIGHT_BIAS);
		FlareManager lensFlare = FlareFactory.createLensFlare();
		Texture sunTexture = Texture.newTexture(new MyFile(new MyFile("res", "lensFlare"), "sun.png")).normalMipMap().create();
		Sun sun = new Sun(sunTexture, 40, light, lensFlare);
		sun.setDirection(WorldSettings.LIGHT_DIR);

		// initialize terrain
		PerlinNoise noise = new PerlinNoise(WorldSettings.OCTAVES, WorldSettings.AMPLITUDE, WorldSettings.ROUGHNESS);
		ColourGenerator colourGen = new ColourGenerator(WorldSettings.TERRAIN_COLS, WorldSettings.COLOUR_SPREAD);
		TerrainGenerator terrainGenerator = new HybridTerrainGenerator(noise, colourGen);
		Terrain terrain = terrainGenerator.generateTerrain(WorldSettings.WORLD_SIZE);
		WaterTileAux water = WaterGenerator.generate(WorldSettings.WORLD_SIZE, WorldSettings.WATER_HEIGHT);

		loader = new Loader();

		List<Entity> treeEntities = new ArrayList<Entity>();
		int treesLoaded = 0;
		while (treesLoaded < 50) {
			Random rand = new Random();
			int terrainX = rand.nextInt(WorldSettings.WORLD_SIZE);
			int terrainZ = rand.nextInt(WorldSettings.WORLD_SIZE);
			float terrainY = terrain.getHeightOfTerrain(terrainX, terrainZ);
			if (terrainY < WorldSettings.WATER_HEIGHT + 5 ||
				terrainX < 20 || terrainX > WorldSettings.WORLD_SIZE - 20 ||
				terrainZ < 20 || terrainZ > WorldSettings.WORLD_SIZE - 20) {
				continue;
			}
			terrainY -= 0.5f; // to prevent objects levitating above the terrain

			ModelData treeData = OBJFileLoader.loadOBJ(new MyFile("pine.obj"));
			RawModel treeRawModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
			TexturedModel treeModel = new TexturedModel(treeRawModel, new ModelTexture(loader.loadTexture("pine")));
			Entity tree = new Entity(treeModel, new Vector3f(terrainX, terrainY, terrainZ), 0, 0, 0, 0.5f);
			treeEntities.add(tree);
			treesLoaded++;				
		}

		List<ParticleSystemComplex> particleSystems = new ArrayList<ParticleSystemComplex>();

		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
		ParticleTexture particleTextureFire = new ParticleTexture(loader.loadTexture("fire"), 8, true);
		ParticleTexture particleTextureSmoke = new ParticleTexture(loader.loadTexture("smoke"), 8, false);

		ParticleSystemComplex particleSystem = new ParticleSystemComplex(particleTexture,
			/* pps */ 50f, /* speed */ 0.5f, /* gravity */ -0.05f, /* life */ 50f, /* scale */ 0.5f);
		particleSystem.setLifeError(0.1f).setSpeedError(0.25f).setScaleError(0.5f).randomizeRotation();
		particleSystems.add(particleSystem);

		ParticleSystemComplex particleSystemFire = new ParticleSystemComplex(particleTextureFire,
			100f, 1f, -0.01f, 2f, 4f);
		particleSystemFire.setLifeError(0.1f).setSpeedError(0.25f).setScaleError(0.5f).randomizeRotation();
		particleSystems.add(particleSystemFire);

		ParticleSystemComplex particleSystemSmoke = new ParticleSystemComplex(particleTextureSmoke,
			100f, 1f, -0.05f, 20f, 2f);
		particleSystemSmoke.setLifeError(0.1f).setSpeedError(0.5f).setScaleError(0.3f).randomizeRotation().setDirection(new Vector3f(-0.5f, -0.5f, -0.5f), 5f);
		// particleSystems.add(particleSystemSmoke);

		Player animatedPlayer = AnimatedModelLoader.loadPlayer(new MyFile(resFolder, GeneralSettings.MODEL_FILE),
				new MyFile(resFolder, GeneralSettings.DIFFUSE_FILE));
		Animation animation = AnimationLoader.loadAnimation(new MyFile(resFolder, GeneralSettings.ANIM_FILE));
		animatedPlayer.doAnimation(animation);
		System.out.println("Scene loadScene.");

		Scene scene = createScene(animatedPlayer, terrainFiles, entityFiles, shinyFiles, sky, sun, terrain, water, treeEntities);
		scene.addParticleSystems(particleSystems);
		return scene;
	}

	private Scene createScene(Player animatedPlayer, MyFile[] terrainFiles, MyFile[] entityFiles, MyFile[] shinyFiles, 
			Skybox sky, Sun sun, Terrain terrain, WaterTileAux water, List<Entity> treeEntities){
		scene = new Scene(animatedPlayer, sky, terrain, water, sun);
		scene.setLightDirection(sun.getLight().getDirection());
		addEntities(scene, entityFiles);
		addEntities(scene, treeEntities);
		addShinyEntities(scene, shinyFiles);
		addTerrains(scene, terrainFiles);
		return scene;
	}

	private void addEntities(Scene scene, List<Entity> treeEntities) {
		for(Entity entity : treeEntities){
			scene.addEntity(entity);
		}
	}

	public static Scene getScene() {
		return scene;
	}

	public Loader getLoader() {
		return loader;
	}

	private void addEntities(Scene scene, MyFile[] entityFiles){
		for(MyFile file : entityFiles){
			SceneEntity entity = entityLoader.loadEntity(file);
			scene.addEntity(entity);
		}
	}

	private void addShinyEntities(Scene scene, MyFile[] entityFiles){
		for(MyFile file : entityFiles){
			SceneEntity entity = entityLoader.loadEntity(file);
			scene.addShiny(entity);
		}
	}

	private void addTerrains(Scene scene, MyFile[] terrainFiles){
		for(MyFile file : terrainFiles){
			SceneEntity entity = entityLoader.loadEntity(file);
			scene.addTerrain(entity);
		}
	}

	private BufferedReader getReader(MyFile file) {
		try {
			return file.getReader();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't find scene file: " + file);
			System.exit(-1);
			return null;
		}
	}

	private void closeReader(BufferedReader reader){
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MyFile[] readEntityFiles(BufferedReader reader, MyFile sceneFile) {
		try {
			String line = reader.readLine();
			if (line.isEmpty()) {
				return new MyFile[0];
			}
			String[] names = line.split(LoaderSettings.SEPARATOR);
			MyFile[] files = new MyFile[names.length];
			for(int i=0;i<files.length;i++){
				files[i] = new MyFile(sceneFile, names[i]);
			}
			return files;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't read scene file: " + sceneFile);
			System.exit(-1);
			return null;
		}
	}
}
