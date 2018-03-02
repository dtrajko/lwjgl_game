package entityRenderers;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import shaders.ShaderProgram;
import shaders.UniformBoolean;
import shaders.UniformMatrix;
import shaders.UniformSampler;
import shaders.UniformVec3;
import shaders.UniformVec4;
import utils.MyFile;

public class EntityShader extends ShaderProgram {

	private static final MyFile VERTEX_SHADER = new MyFile("entityRenderers", "entityVertex.txt");
	private static final MyFile FRAGMENT_SHADER = new MyFile("entityRenderers", "entityFragment.txt");

	public UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	public UniformBoolean hasExtraMap = new UniformBoolean("hasExtraMap");
	public UniformVec3 lightDirection = new UniformVec3("lightDirection");
	public UniformVec4 plane = new UniformVec4("plane");

	private UniformSampler diffuseMap = new UniformSampler("diffuseMap");
	private UniformSampler extraMap = new UniformSampler("extraMap");

	private int location_transformationMatrix;
	private int location_offset;

	public EntityShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position", "in_textureCoords", "in_normal");
		super.storeAllUniformLocations(projectionViewMatrix, diffuseMap, extraMap, hasExtraMap,
				lightDirection, plane);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		diffuseMap.loadTexUnit(0);
		extraMap.loadTexUnit(1);
		super.stop();
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadOffset(float x, float y) {
		super.load2DVector(location_offset, new Vector2f(x, y));
	}
}
