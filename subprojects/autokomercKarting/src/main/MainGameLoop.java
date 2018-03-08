package main;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import input.InputHelper;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import sunRenderer.Sun;
import sunRenderer.SunRenderer;
import renderEngine.Game;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import textures.Texture;
import utils.MyFile;

public class MainGameLoop {

	public static void main(String[] args) {

		List<Entity> entities = new ArrayList<Entity>();
		List<Terrain> terrains = new ArrayList<Terrain>();
		List<Light> lights = new ArrayList<Light>();

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		/**************** BEGIN TERRAIN TEXTURE STUFF ****************/

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("race/background"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("race/red"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("race/green"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("race/blue"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("race/race_track_blend_map"));

		/**************** END TERRAIN TEXTURE STUFF ****************/

		// terrains
		Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap, "race/race_track_heightmap");

		// player
		RawModel steveModelRaw = OBJFileLoader.loadOBJ("steve", loader);
		TexturedModel steveModel = new TexturedModel(steveModelRaw, new ModelTexture(loader.loadTexture("steve")));
		Player player = new Player(steveModel, new Vector3f(-285, terrain.getHeightOfTerrain(-285, -70) + 2, -70), 0, 180, 0, 1f);

		// camera
		Camera camera = new Camera(player, terrain);
		camera.setPerspective(Camera.Perspective.FIRST_PERSON);

		MasterRenderer renderer = new MasterRenderer(loader, camera);

		// lights
		Light sun_light = new Light(new Vector3f(15000, 10000, 15000), new Vector3f(1f, 1f, 1f)); // world light (sun)
		lights.add(sun_light);

		terrains.add(terrain);
		entities.add(player);

		//init sun and set sun direction
		MyFile flareFolder = new MyFile("res", "lensFlare");
		Texture sun = Texture.newTexture(new MyFile(flareFolder, "sun.png")).normalMipMap().create();
		Sun theSun = new Sun(sun, 40);
		SunRenderer sunRenderer = new SunRenderer();
		theSun.setDirection(WorldSettings.LIGHT_DIR);

		DisplayManager.startFPS();

		while(!Display.isCloseRequested()) {

			DisplayManager.updateFPS();
			DisplayManager.updateDisplayMode();
			DisplayManager.updateDisplay();

			InputHelper.update();
			Game.checkIfRunning();

			if (Game.isRunning()) {

				player.move(terrain);
				camera.move();

				GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

				renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));
				sunRenderer.render(theSun, camera);
			}
		}

		sunRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
