package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Camera {

	private Vector3f position = new Vector3f(0, 8, 30);
	private float pitch;
	private float yaw;
	private float roll;

	public Camera() {}

	public void move() {
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x -= 0.05f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x += 0.05f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.y -= 0.05f;
			if (position.y < 0.2f) {
				position.y = 0.2f;
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.y += 0.05f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			position.z -= 0.05f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			position.z += 0.05f;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
}
