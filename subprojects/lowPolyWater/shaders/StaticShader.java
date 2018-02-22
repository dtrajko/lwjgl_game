package shaders;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;

public class StaticShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String GEOMETRY_FILE = "src/shaders/geometryShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";
	
	private int location_projectionViewMatrix;
	private int location_time;
	private int location_cameraPosition;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, GEOMETRY_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionViewMatrix = super.getUniformLocation("projectionViewMatrix");
		location_time = super.getUniformLocation("time");
		location_cameraPosition = super.getUniformLocation("cameraPosition");
	}
	
	public void loadProjectionViewMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionViewMatrix, matrix);
	}
	
	public void loadTime(float time){
		super.loadFloat(location_time, time);
	}
	
	public void loadCameraPosition(Camera camera){
		super.loadVector(location_cameraPosition, camera.getPosition());
	}
	

}
