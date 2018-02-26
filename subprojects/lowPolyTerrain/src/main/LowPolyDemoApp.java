package main;

import flatTerrain.FlatTerrainGenerator;
import generation.ColourGenerator;
import generation.PerlinNoise;
import geometryTerrain.GeometryTerrainGenerator;
import indicesGenerators.StandardIndexGenerator;
import rendering.Light;
import rendering.RenderEngine;
import specialTerrain.SpecialTerrainGenerator;
import splitTerrain.SplitTerrainGenerator;
import terrains.Terrain;
import terrains.TerrainGenerator;

public class LowPolyDemoApp {

	public static void main(String[] args) {
		
		//init engine and scene objects
		RenderEngine engine = new RenderEngine(Configs.FPS_CAP);
		Camera camera = new Camera();
		Light light = new Light(Configs.LIGHT_POS, Configs.LIGHT_COL, Configs.LIGHT_BIAS);

		//init generators for heights and colours
		PerlinNoise noise = new PerlinNoise(Configs.OCTAVES, Configs.AMPLITUDE, Configs.ROUGHNESS);
		ColourGenerator colourGen = new ColourGenerator(Configs.TERRAIN_COLS, Configs.COLOUR_SPREAD);

		//init the 4 different methods for generating a low-poly terrain.
		TerrainGenerator geomGenerator = new GeometryTerrainGenerator(noise, colourGen, new StandardIndexGenerator());
		TerrainGenerator flatGenerator = new FlatTerrainGenerator(noise, colourGen, new StandardIndexGenerator());
		TerrainGenerator specialGenerator = new SpecialTerrainGenerator(noise, colourGen);
		TerrainGenerator splitGenerator = new SplitTerrainGenerator(noise, colourGen);

		Terrain terrain = splitGenerator.generateTerrain(Configs.TERRAIN_SIZE);

		while (!engine.getWindow().isCloseRequested()) {
			camera.move();

			engine.render(terrain, camera, light);
			
		}

		terrain.delete();
		geomGenerator.cleanUp();
		flatGenerator.cleanUp();
		splitGenerator.cleanUp();
		specialGenerator.cleanUp();
		

		engine.close();

	}

}
