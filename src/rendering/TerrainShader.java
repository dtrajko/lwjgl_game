package rendering;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;
import shaders.UniformMatrix;
import shaders.UniformVec2;
import shaders.UniformVec3;
import shaders.UniformVec4;
import utils.MyFile;

/**
 * Represents the shader program that is used for rendering the terrain.
 * 
 * @author Karl
 *
 */
public class TerrainShader extends ShaderProgram {

	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformVec3 lightColour = new UniformVec3("lightColour");
	protected UniformVec2 lightBias = new UniformVec2("lightBias");
	protected UniformVec4 plane = new UniformVec4("plane");

	private int location_transformationMatrix;
	private int location_toShadowMapSpace;
	private int location_shineDamper;
	private int location_reflectivity;

	public TerrainShader(MyFile vertexFile, MyFile fragmentFile) {
		super(vertexFile, fragmentFile);
		super.storeAllUniformLocations(projectionViewMatrix, lightDirection, lightColour, lightBias, plane);
	}

	public TerrainShader(MyFile vertexFile, MyFile geometryFile, MyFile fragmentFile) {
		super(vertexFile, geometryFile, fragmentFile);
		super.storeAllUniformLocations(projectionViewMatrix, lightDirection, lightColour, lightBias, plane);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadToShadowSpaceMatrix(Matrix4f matrix) {
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
}
