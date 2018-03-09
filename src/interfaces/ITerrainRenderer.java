package interfaces;

import org.lwjgl.util.vector.Vector4f;

import terrains.HeightMapTerrain;
import utils.Light;

public interface ITerrainRenderer {

	void render(ITerrain terrain, ICamera camera, Light light, Vector4f clipPlane);

	void cleanUp();
}
