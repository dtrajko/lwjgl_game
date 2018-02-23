package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import terrains.Terrain;

public class Camera {

	private static final float ZOOM_COEF = 0.1f;
	private static final float PITCH_COEF = 0.15f;
	
	private static final float DISTANCE_FROM_PLAYER = 60;
	private static final float PITCH_THIRD_PERSON = 20;
	private static final float PITCH_FIRST_PERSON = 15;

	private float distanceFromPlayer = 60;
	private float angleAroundPlayer = 0;
	private Vector3f position = new Vector3f(0, 30, 30);
	private float pitch = PITCH_THIRD_PERSON;
	private float yaw = 0;
	private float roll;
	private Player player;
	private Terrain terrain = null;
	
	private enum Perspective {
		FIRST_PERSON,
		THIRD_PERSON,
	}
	
	private Perspective currentPerspective = Perspective.THIRD_PERSON;

	public Camera(Player player) {
		this.player = player;
	}

	public Camera(Player player, Terrain terrain) {
		this.player = player;
		this.terrain = terrain;
	}

	public void move() {
		checkInputs();
		this.calculateZoom();
		this.calculatePitch();
		this.calculateAngleAroundPlayer();
		float horizontalDistance = this.calculateHorizontalDistance();
		float verticalDistance = this.calculateVerticalDistance();
		this.calculateCameraPosition(horizontalDistance, verticalDistance);
		// change camera angle to point towards the player
		this.yaw = 180 - (player.getRotY() + this.angleAroundPlayer);
	}

	public void invertPitch() {
		this.pitch = -pitch;
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

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = player.getRotY() + this.angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance - player.getVerticalOffset();
		
		if (terrain != null && terrain.getHeightOfTerrain(position.x, position.z) > position.y) {
			position.y = terrain.getHeightOfTerrain(position.x, position.z) + 5;
		}
	}

	private float calculateHorizontalDistance() {
		return (float) (this.distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		float verticalDistance;
		if (this.currentPerspective == Camera.Perspective.FIRST_PERSON) {
			verticalDistance = player.getHeight();
		} else {
			verticalDistance = (float) (this.distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		}
		return verticalDistance;
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * ZOOM_COEF;
		distanceFromPlayer -= zoomLevel;
	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * PITCH_COEF;
			pitch -= pitchChange;
			if (pitch < 0.05f) {
				// pitch = 0.05f;
			}
		}
	}

	private void calculateAngleAroundPlayer() {
		if (Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}

	public void checkInputs() {

		// camera perspective
		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			if (this.currentPerspective == Camera.Perspective.THIRD_PERSON) {
				this.currentPerspective = Camera.Perspective.FIRST_PERSON;
				this.distanceFromPlayer = 0;
				this.pitch = this.PITCH_FIRST_PERSON;
				this.player.setRenderingEnabled(false);
			} else if (this.currentPerspective == Camera.Perspective.FIRST_PERSON) {
				this.currentPerspective = Camera.Perspective.THIRD_PERSON;
				this.distanceFromPlayer = this.DISTANCE_FROM_PLAYER;
				this.pitch = this.PITCH_THIRD_PERSON;
				this.player.setRenderingEnabled(true);
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			// move left sidewise
		} else if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			// move right sidewise
		}
	}
}
