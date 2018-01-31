package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import terrains.Terrain;

public class Camera {

	private float distanceFromPlayer = 60;
	private float angleAroundPlayer = 0;
	private Vector3f position = new Vector3f(0, 30, 30);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;
	private Player player;
	private Terrain terrain = null;

	public Camera(Player player) {
		this.player = player;
	}

	public Camera(Player player, Terrain terrain) {
		this.player = player;
		this.terrain = terrain;
	}

	public void move() {
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
		return (float) (this.distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
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
}
