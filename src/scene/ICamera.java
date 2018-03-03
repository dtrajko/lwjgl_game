package scene;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public interface ICamera {
	
	public Matrix4f getViewMatrix();
	public Matrix4f getProjectionMatrix();
	public Matrix4f getProjectionViewMatrix();
	public void move();
	public Vector3f getPosition();
	void reflect(float height);
	void reflect();
	public void switchToFace(int i);
	public void setScene(Scene scene);
	public double getPitch();
	public double getYaw();
	public double getRoll();
	public float getNearPlane();
	public float getFarPlane();

}
