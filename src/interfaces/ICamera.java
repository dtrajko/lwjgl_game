package interfaces;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import scene.Scene;

public interface ICamera {

	Matrix4f getViewMatrix();
	Matrix4f getProjectionMatrix();
	Matrix4f getProjectionViewMatrix();
	Vector3f getPosition();
	void move();
	void togglePerspective();
	void reflect(float height);
	void reflect();
	void switchToFace(int i);
	void setScene(Scene scene);
	void setPitch(float pitch);
	float getPitch();
	float getNearPlane();
	float getFarPlane();
	double getYaw();
	double getRoll();
}
