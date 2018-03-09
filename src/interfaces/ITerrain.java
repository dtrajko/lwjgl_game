package interfaces;

import org.lwjgl.util.vector.Vector4f;

import models.RawModel;
import openglObjects.Vao;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import utils.Light;

public interface ITerrain {

	void render(ICamera camera, Light light, Vector4f clipPlane);
	Vao getVao();
	RawModel getModel();
	TerrainTexturePack getTexturePack();
	TerrainTexture getBlendMap();
	int getVertexCount();
	float getHeightOfTerrain(float worldX, float worldZ);
	float getX();
	float getZ();
}
