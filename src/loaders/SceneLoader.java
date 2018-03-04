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
		closeReader(reader);
		Skybox sky = skyLoader.loadSkyBox(new MyFile(sceneFile, LoaderSettings.SKYBOX_FOLDER));

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

		Loader loader = new Loader();
		List<Entity> treeEntities = new ArrayList<Entity>();
		int treesLoaded = 0;
		while (treesLoaded < 100) {
			Random rand = new Random();
			int terrainX = rand.nextInt(200);
			int terrainZ = rand.nextInt(200);
			float terrainY = terrain.getHeightOfTerrain(terrainX, terrainZ);
			if (terrainY < WorldSettings.WATER_HEIGHT + 2) {
				continue;
			}
			ModelData treeData = OBJFileLoader.loadOBJ(new MyFile("tree.obj"));
			RawModel treeRawModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
			TexturedModel treeModel = new TexturedModel(treeRawModel, new ModelTexture(loader.loadTexture("tree")));
			Entity tree = new Entity(treeModel, new Vector3f(terrainX, terrainY, terrainZ), 0, 0, 0, 1f);
			treeEntities.add(tree);
			treesLoaded++;
		}

		Player animatedPlayer = AnimatedModelLoader.loadPlayer(new MyFile(resFolder, GeneralSettings.MODEL_FILE),
				new MyFile(resFolder, GeneralSettings.DIFFUSE_FILE));
		Animation animation = AnimationLoader.loadAnimation(new MyFile(resFolder, GeneralSettings.ANIM_FILE));
		animatedPlayer.doAnimation(animation);
		System.out.println("Scene loadScene.");
		return createScene(animatedPlayer, terrainFiles, entityFiles, shinyFiles, sky, sun, terrain, water, treeEntities);
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
