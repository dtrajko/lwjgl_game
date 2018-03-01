package scene;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import animatedModel.AnimatedModel;
import entities.Player;
import extra.Camera;
import skybox.Skybox;
import textures.Texture;
import utils.MyFile;
import water.WaterTile;

/**
 * Represents all the stuff in the scene (just the camera, light, and model
 * really).
 * 
 * @author Karl
 *
 */
public class Scene {

	private List<Entity> standardEntities = new ArrayList<Entity>();
	private List<Entity> reflectableEntities = new ArrayList<Entity>();
	private List<Entity> underwaterEntities = new ArrayList<Entity>();
	private List<Entity> importantEntities = new ArrayList<Entity>();
	private List<Entity> shinyEntities = new ArrayList<Entity>();

	private List<WaterTile> waterTiles = new ArrayList<WaterTile>();

	private final ICamera camera;

	private final Player animatedPlayer;

	private Vector3f lightDirection = new Vector3f(0, -1, 0);

	private Skybox sky;

	private Texture environmentMap;

	private float waterHeight = -0.1f; // should set elsewhere

	public Scene(Player animatedPlayer, Skybox sky) {
		this.camera = new Camera();
		camera.setScene(this);
		this.sky = sky;
		this.animatedPlayer = animatedPlayer;
		System.out.println("animatedModel class: " + animatedPlayer.getClass());
		environmentMap = Texture.newEmptyCubeMap(512);
		waterTiles.add(new WaterTile(-20, 6, waterHeight));
		waterTiles.add(new WaterTile(-10, 6, waterHeight));
		waterTiles.add(new WaterTile(0, 6, waterHeight));
		waterTiles.add(new WaterTile(10, 6, waterHeight));
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
		for (Entity entity : standardEntities) {
			entity.delete();
		}
		animatedPlayer.delete();
		environmentMap.delete();
	}

	public void addEntity(Entity entity) {
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

	public void addShiny(Entity entity){
		if(entity.isSeenUnderWater()){
			underwaterEntities.add(entity);
		}
		if(entity.hasReflection()){
			reflectableEntities.add(entity);
		}
		shinyEntities.add(entity);
	}

	public void addTerrain(Entity terrain) {
		standardEntities.add(terrain);
		importantEntities.add(terrain);
		reflectableEntities.add(terrain);
		underwaterEntities.add(terrain);
	}

	public List<Entity> getImportantEntities() {
		return importantEntities;
	}

	public Skybox getSky() {
		return sky;
	}

	public List<Entity> getReflectedEntities() {
		return reflectableEntities;
	}

	public List<Entity> getUnderwaterEntities() {
		return underwaterEntities;
	}

	public List<Entity> getAllEntities() {
		return standardEntities;
	}

	public List<Entity> getShinyEntities() {
		return shinyEntities;
	}

	public float getWaterHeight(){
		return waterHeight;
	}

	public List<WaterTile> getWater() {
		return waterTiles;
	}
}
