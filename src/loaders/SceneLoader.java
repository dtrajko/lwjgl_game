package loaders;

import java.io.BufferedReader;
import java.io.IOException;

import animatedModel.AnimatedModel;
import animation.Animation;
import entities.Player;
import extra.Camera;
import generation.ColourGenerator;
import generation.PerlinNoise;
import hybridTerrain.HybridTerrainGenerator;
import main.GeneralSettings;
import main.WorldSettings;
import scene.SceneEntity;
import scene.Scene;
import skybox.Skybox;
import terrains.Terrain;
import terrains.TerrainGenerator;
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
		
		//init terrain
		Light light = new Light(WorldSettings.LIGHT_DIR, WorldSettings.LIGHT_COL, WorldSettings.LIGHT_BIAS);
		PerlinNoise noise = new PerlinNoise(WorldSettings.OCTAVES, WorldSettings.AMPLITUDE, WorldSettings.ROUGHNESS);
		ColourGenerator colourGen = new ColourGenerator(WorldSettings.TERRAIN_COLS, WorldSettings.COLOUR_SPREAD);
		TerrainGenerator terrainGenerator = new HybridTerrainGenerator(noise, colourGen);
		Terrain terrain = terrainGenerator.generateTerrain(WorldSettings.WORLD_SIZE);
		WaterTileAux water = WaterGenerator.generate(WorldSettings.WORLD_SIZE, WorldSettings.WATER_HEIGHT);

		Player animatedPlayer = AnimatedModelLoader.loadPlayer(new MyFile(resFolder, GeneralSettings.MODEL_FILE),
				new MyFile(resFolder, GeneralSettings.DIFFUSE_FILE));
		Animation animation = AnimationLoader.loadAnimation(new MyFile(resFolder, GeneralSettings.ANIM_FILE));
		animatedPlayer.doAnimation(animation);
		System.out.println("Scene loadScene.");
		return createScene(animatedPlayer, terrainFiles, entityFiles, shinyFiles, sky, light, terrain, water);
	}

	private Scene createScene(Player animatedPlayer, MyFile[] terrainFiles, MyFile[] entityFiles, MyFile[] shinyFiles, Skybox sky, Light light, Terrain terrain, WaterTileAux water){
		scene = new Scene(animatedPlayer, sky, light, terrain, water);
		scene.setLightDirection(light.getDirection());
		addEntities(scene, entityFiles);
		addShinyEntities(scene, shinyFiles);
		addTerrains(scene, terrainFiles);
		return scene;
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
