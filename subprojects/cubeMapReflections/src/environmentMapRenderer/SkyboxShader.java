package environmentMapRenderer;
 
import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;
 
public class SkyboxShader extends ShaderProgram{
 
    private static final String VERTEX_FILE = "src/environmentMapRenderer/skyboxVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/environmentMapRenderer/skyboxFragmentShader.txt";
     
    private int location_projectionViewMatrix;
     
    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
 
    public void loadProjectionViewMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionViewMatrix, matrix);
    }
     
    @Override
    protected void getAllUniformLocations() {
    	location_projectionViewMatrix = super.getUniformLocation("projectionViewMatrix");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
 
}