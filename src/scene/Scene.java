package scene;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Player;
import extra.Camera;
import guis.GuiTexture;
import interfaces.ICamera;
import interfaces.ITerrain;
import lensFlare.FlareManager;
import particles.ParticleSystemComplex;
import racetrack.Racetrack;
import skybox.Skybox;
import sunRenderer.Sun;
import terrains.Terrain;
import textures.Texture;
import utils.Light;
import water.WaterTile;
import water.WaterTileVao;

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
	private List<Entity> additionalEntities = new ArrayList<Entity>();

	private List<WaterTile> waterTiles = new ArrayList<WaterTile>();
	private List<WaterTileVao> waters = new ArrayList<WaterTileVao>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();

	private final ICamera camera;

	private final Player animatedPlayer;

	private Vector3f lightDirection = new Vector3f(0, -1, 0);

	private Skybox sky;
	private ITerrain terrain;
	private Light light;
	private Sun sun;
	private FlareManager lensFlare;

	private Texture environmentMap;

	private float waterHeight = -0.1f; // should set elsewhere

	private List<ParticleSystemComplex> particleSystems = new ArrayList<ParticleSystemComplex>();

	private Racetrack racetrack = null;

	public Scene(Player animatedPlayer, Skybox sky, ITerrain terrain, List<WaterTileVao> waters, Sun sun) {
		this.camera = new Camera();
		camera.setScene(this);
		this.sky = sky;
		this.light = sun.getLight();
		this.terrain = terrain;
		this.waters = waters;
		this.sun = sun;
		this.lensFlare = sun.getLensFlare();
		this.animatedPlayer = animatedPlayer;
		environmentMap = Texture.newEmptyCubeMap(256);

		// adjust camera settings for the race track project
		camera.togglePerspective();
	}

	public void addParticleSystems(List<ParticleSystemComplex> particleSystems) {
		this.particleSystems = particleSystems;
	}

	public List<ParticleSystemComplex> getParticleSystems() {
		return particleSystems;
	}

	public Texture getEnvironmentMap(){
		return environmentMap;
	}
	
	public void update() {
		animatedPlayer.update(terrain);
		camera.move();
		if (racetrack != null) {
			racetrack.update();
		}

		// particleSystems.get(1).generateParticles(new Vector3f(110, terrain.getHeightOfTerrain(110, 110), 110));
		// particleSystems.get(2).generateParticles(new Vector3f(110, terrain.getHeightOfTerrain(110, 110) + 2, 110));
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
		sun.delete();
		for (SceneEntity entity : standardEntities) {
			entity.delete();
		}
		animatedPlayer.delete();
		environmentMap.delete();
	}

	public void addEntity(Entity entity) {
		additionalEntities.add(entity);
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

	public ITerrain getTerrain() {
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

	public List<Entity> getAdditionalEntities() {
		return additionalEntities;
	}

	public float getWaterHeight(){
		return waterHeight;
	}

	public List<WaterTile> getWater() {
		return waterTiles;
	}

	public List<WaterTileVao> getWatersVao() {
		return waters;
	}

	public Sun getSun() {
		return sun;
	}

	public FlareManager getLensFlare() {
		return lensFlare;
	}

	public void setRacetrack(Racetrack racetrack) {
		this.racetrack = racetrack;
	}

	public Racetrack getRacetrack() {
		return this.racetrack;
	}

	public List<GuiTexture> getGuiElements() {
		return this.guis;
	}
}
