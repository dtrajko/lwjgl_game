package scene;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Player;
import extra.Camera;
import skybox.Skybox;
import terrains.Terrain;
import textures.Texture;
import utils.Light;
import water.WaterTile;
import water.WaterTileAux;

/**
 * Represents all the stuff in the scene (just the camera, light, and model
 * really).
 * 
 * @author Karl
 *
 */
public class Scene {

	private List<SceneEntity> standardEntities = new ArrayList<SceneEntity>();
	private List<SceneEntity> reflectableEntities = new ArrayList<SceneEntity>();
	private List<SceneEntity> underwaterEntities = new ArrayList<SceneEntity>();
	private List<SceneEntity> importantEntities = new ArrayList<SceneEntity>();
	private List<SceneEntity> shinyEntities = new ArrayList<SceneEntity>();

	private List<WaterTile> waterTiles = new ArrayList<WaterTile>();

	private final ICamera camera;

	private final Player animatedPlayer;

	private Vector3f lightDirection = new Vector3f(0, -1, 0);

	private Skybox sky;
	private Terrain terrain;
	private WaterTile water;
	private WaterTileAux waterAux;
	private Light light;

	private Texture environmentMap;

	private float waterHeight = -0.1f; // should set elsewhere

	public Scene(Player animatedPlayer, Skybox sky, Light light, Terrain terrain, WaterTileAux water) {
		this.camera = new Camera();
		camera.setScene(this);
		this.sky = sky;
		this.light = light;
		this.terrain = terrain;
		this.waterAux = water;
		this.animatedPlayer = animatedPlayer;
		environmentMap = Texture.newEmptyCubeMap(256);
		waterTiles.add(new WaterTile(-16, 6, waterHeight));
		waterTiles.add(new WaterTile(-6, 6, waterHeight));
		waterTiles.add(new WaterTile(4, 6, waterHeight));
	}

	public Texture getEnvironmentMap(){
		return environmentMap;
	}

	/**
	 * @return The scene's camera.
	 */
	public ICamera getCamera() {
		return camera;
	}
	
	public Light getLight() {
		return light;
	}

	public Player getAnimatedPlayer() {
		return animatedPlayer;
	}

	/**
	 * @return The direction of the light as a vector.
	 */
	public Vector3f getLightDirection() {
		return lightDirection;
	}

	public void setLightDirection(Vector3f lightDir) {
		this.lightDirection.set(lightDir);
	}

	public void delete() {
		sky.delete();
		for (SceneEntity entity : standardEntities) {
			entity.delete();
		}
		animatedPlayer.delete();
		environmentMap.delete();
	}

	public void addEntity(SceneEntity entity) {
		standardEntities.add(entity);
		if(entity.isSeenUnderWater()){
			underwaterEntities.add(entity);
		}
		if(entity.hasReflection()){
			reflectableEntities.add(entity);
		}
		if(entity.isImportant()){
			importantEntities.add(entity);
		}
	}

	public void addShiny(SceneEntity entity){
		if(entity.isSeenUnderWater()){
			underwaterEntities.add(entity);
		}
		if(entity.hasReflection()){
			reflectableEntities.add(entity);
		}
		shinyEntities.add(entity);
	}

	public void addTerrain(SceneEntity terrain) {
		standardEntities.add(terrain);
		importantEntities.add(terrain);
		reflectableEntities.add(terrain);
		underwaterEntities.add(terrain);
	}

	public List<SceneEntity> getImportantEntities() {
		return importantEntities;
	}

	public Skybox getSky() {
		return sky;
	}

	public Terrain getTerrain() {
		return terrain;
	}

	public List<SceneEntity> getReflectedEntities() {
		return reflectableEntities;
	}

	public List<SceneEntity> getUnderwaterEntities() {
		return underwaterEntities;
	}

	public List<SceneEntity> getAllEntities() {
		return standardEntities;
	}

	public List<SceneEntity> getShinyEntities() {
		return shinyEntities;
	}

	public float getWaterHeight(){
		return waterHeight;
	}

	public List<WaterTile> getWater() {
		return waterTiles;
	}

	public WaterTileAux getWaterAux() {
		return waterAux;
	}
}
