package terrains;

import org.lwjgl.util.vector.Vector4f;

import main.WorldSettings;
import openglObjects.Vao;
import rendering.TerrainRenderer;
import scene.ICamera;
import utils.Light;

public class Terrain {
	
	private final Vao vao;
	private final int vertexCount;
	private final TerrainRenderer renderer;
	private final float[][] heights;
	private final float WATER_HEIGHT = WorldSettings.WATER_HEIGHT;

	public Terrain(Vao vao, int vertexCount, TerrainRenderer renderer, float[][] heights){
		this.vao = vao;
		this.vertexCount = vertexCount;
		this.renderer = renderer;
		this.heights = heights;
	}
	
	public int getVertexCount(){
		return vertexCount;
	}
	
	public Vao getVao(){
		return vao;
	}
	
	public void render(ICamera camera, Light light, Vector4f clipPlane){
		renderer.render(this, camera, light, clipPlane);
	}
	
	public void delete(){
		vao.delete();
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		int intX = (int) Math.floor(worldX);
		int intZ = (int) Math.floor(worldZ);
		float worldY = 0;
		if (intX < 0 || intX >= heights.length - 1 ||
			intZ < 0 || intZ >= heights.length - 1) {
			return worldY;
		}
		worldY = this.heights[intZ][intX];
		worldY += 0.2f; // a small adjustment
		if (worldY < WATER_HEIGHT) {
			worldY = WATER_HEIGHT;
		}
		return worldY;
	}
}
