package loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import animation.Animation;
import audio.AudioMaster;
import audio.Source;
import entities.Entity;
import entities.Player;
import factories.FontFactory;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import generation.ColourGenerator;
import generation.PerlinNoise;
import guis.GuiTexture;
import hybridTerrain.HybridTerrainGenerator;
import interfaces.ITerrain;
import lensFlare.FlareFactory;
import lensFlare.FlareManager;
import main.GeneralSettings;
import main.WorldSettings;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.ParticleSystemComplex;
import particles.ParticleTexture;
import racetrack.Racetrack;
import scene.SceneEntity;
import scene.Scene;
import skybox.Skybox;
import sunRenderer.Sun;
import terrains.HeightMapTerrain;
import terrains.Terrain;
import terrains.TerrainGenerator;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import textures.Texture;
import utils.Light;
import utils.MyFile;
import water.WaterGenerator;
import water.WaterTileVao;

public class SceneLoader {

	private EntityLoader entityLoader;
	private SkyboxLoader skyLoader;
	private static Scene scene;
	private RawModelLoader rawModelLoader;

	public SceneLoader(EntityLoader entityLoader, SkyboxLoader skyLoader, RawModelLoader rawModelLoader) {
		this.entityLoader = entityLoader;
		this.skyLoader = skyLoader;
		this.rawModelLoader = rawModelLoader;
	}

	public Scene loadScene(MyFile resFolder, MyFile sceneFile) {

		List<WaterTileVao> waters = new ArrayList<WaterTileVao>();
		List<Entity> additionalEntities = new ArrayList<Entity>();
		List<ParticleSystemComplex> particleSystems = new ArrayList<ParticleSystemComplex>();

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

		WaterTileVao water = WaterGenerator.generate(WorldSettings.WORLD_SIZE, WorldSettings.WATER_HEIGHT);
		waters.add(water);

		additionalEntities = createAdditionalEntities(additionalEntities, terrain);
		// particleSystems = createParticleSystems(particleSystems);

		Player animatedPlayer = AnimatedModelLoader.loadPlayer(new MyFile(resFolder, GeneralSettings.MODEL_FILE),
			new MyFile(resFolder, GeneralSettings.DIFFUSE_FILE), new Vector3f(100f, 0f, 100f), new Vector3f(0, 0, 0), 0.12f);
		Animation animation = AnimationLoader.loadAnimation(new MyFile(resFolder, GeneralSettings.ANIM_FILE));
		animatedPlayer.doAnimation(animation);
		System.out.println("Scene loadScene.");

		Scene scene = createScene(animatedPlayer, sky, sun, terrain, waters, additionalEntities);
		scene.addParticleSystems(particleSystems);
		return scene;
	}

	public Scene loadSceneRaceTrack(MyFile resFolder) {

		List<WaterTileVao> waters = new ArrayList<WaterTileVao>();
		List<Entity> additionalEntities = new ArrayList<Entity>();
		List<ParticleSystemComplex> particleSystems = new ArrayList<ParticleSystemComplex>();
		List<GuiTexture> guis = new ArrayList<GuiTexture>();

		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);

		Skybox sky = skyLoader.loadSkyBox(new MyFile(new MyFile("skybox"), LoaderSettings.SKYBOX_FOLDER_II));

		// initialize sun and lens flare and set sun direction
		Light light = new Light(WorldSettings.LIGHT_DIR, WorldSettings.LIGHT_COL, WorldSettings.LIGHT_BIAS);
		FlareManager lensFlare = FlareFactory.createLensFlare();
		Texture sunTexture = Texture.newTexture(new MyFile(new MyFile("res", "lensFlare"), "sun.png")).normalMipMap().create();
		Sun sun = new Sun(sunTexture, 40, light, lensFlare);
		sun.setDirection(WorldSettings.LIGHT_DIR);

		TerrainTexture backgroundTexture = new TerrainTexture(rawModelLoader.loadTexture("race/black_background"));
		TerrainTexture rTexture = new TerrainTexture(rawModelLoader.loadTexture("race/red_curb"));
		TerrainTexture gTexture = new TerrainTexture(rawModelLoader.loadTexture("race/green_start"));
		TerrainTexture bTexture = new TerrainTexture(rawModelLoader.loadTexture("race/blue_asfalt"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(rawModelLoader.loadTexture("race/race_track_blend_map"));
		HeightMapTerrain terrain = new HeightMapTerrain(0f, 0f, rawModelLoader, texturePack, blendMap, "race/race_track_heightmap");

		// additionalEntities = createAdditionalEntities(additionalEntities, terrain);
		// particleSystems = createParticleSystems(particleSystems);

		Player animatedPlayer = AnimatedModelLoader.loadPlayer(new MyFile(resFolder, GeneralSettings.MODEL_FILE),
			new MyFile(resFolder, GeneralSettings.DIFFUSE_FILE), new Vector3f(114, 0, 206), new Vector3f(0, 180, 0), 0.2f);
		animatedPlayer.setProperties();
		Animation animation = AnimationLoader.loadAnimation(new MyFile(resFolder, GeneralSettings.ANIM_FILE));
		animatedPlayer.doAnimation(animation);

		Scene scene = createScene(animatedPlayer, sky, sun, terrain, waters, additionalEntities);
		scene.addParticleSystems(particleSystems);

		Racetrack racetrack = new Racetrack(scene.getAnimatedPlayer());
		scene.setRacetrack(racetrack);

		TextMaster.init(this.rawModelLoader);

		FontType font = FontFactory.getFont("candara");
		GUIText guiText = new GUIText("", 2.5f, font, new Vector2f(0.38f, 0.9f), 1f, false);
		guiText.setColour(1.0f, 1.0f, 0.9f);
		TextMaster.setGuiText(0, guiText);

		return scene;
	}

	public void cleanUp() {
		// do cleanup here
	}

	private List<Entity> createAdditionalEntities(List<Entity> additionalEntities, ITerrain terrain) {
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
			RawModel treeRawModel = rawModelLoader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
			TexturedModel treeModel = new TexturedModel(treeRawModel, new ModelTexture(rawModelLoader.loadTexture("pine")));
			Entity tree = new Entity(treeModel, new Vector3f(terrainX, terrainY, terrainZ), 0, 0, 0, 0.5f);
			additionalEntities.add(tree);
			treesLoaded++;
		}
		return additionalEntities;
	}

	private List<ParticleSystemComplex> createParticleSystems(List<ParticleSystemComplex> particleSystems) {
		ParticleTexture particleTextureFire = new ParticleTexture(rawModelLoader.loadTexture("fire"), 8, true);
		ParticleSystemComplex particleSystemFire = new ParticleSystemComplex(particleTextureFire,
			100f, 1f, -0.01f, 2f, 4f);
		particleSystemFire.setLifeError(0.1f).setSpeedError(0.25f).setScaleError(0.5f).randomizeRotation();
		particleSystems.add(particleSystemFire);
		return particleSystems;
	}

	private Scene createScene(Player animatedPlayer, Skybox sky, Sun sun, ITerrain terrain, List<WaterTileVao> waters, List<Entity> additionalEntities){
		scene = new Scene(animatedPlayer, sky, terrain, waters, sun);
		scene.setLightDirection(sun.getLight().getDirection());
		addEntities(scene, additionalEntities);
		return scene;
	}

	private void addEntities(Scene scene, List<Entity> additionalEntities) {
		for(Entity entity : additionalEntities){
			scene.addEntity(entity);
		}
	}

	public static Scene getScene() {
		return scene;
	}

	public RawModelLoader getLoader() {
		return rawModelLoader;
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
