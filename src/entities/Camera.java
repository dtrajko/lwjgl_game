package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Camera {

	private Vector3f position = new Vector3f(0, 8, 30);
	private float pitch;
	private float yaw;
	private float roll;
	
	private float speed = 1.0f;

	public Camera() {}

	public void move() {
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			speed = 2.0f;
		} else {
			speed = 1.0f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			// position.x -= 0.1f * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			// position.x += 0.1f * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			position.y += 0.05f * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			position.y -= 0.05f * speed;
			if (position.y < 0.2f) {
				position.y = 0.2f;
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.z -= 0.1f * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.z += 0.1f * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x -= 0.1f * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += 0.1f * speed;
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
