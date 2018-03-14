package interfaces;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import terrains.HeightMapTerrain;
import terrains.Terrain;
import utils.Light;

public interface ITerrainRenderer {

	void render(ITerrain terrain, ICamera camera, Light light, Vector4f clipPlane);

	void cleanUp();

	void render(List<ITerrain> terrains, Matrix4f toShadowSpace);
}
