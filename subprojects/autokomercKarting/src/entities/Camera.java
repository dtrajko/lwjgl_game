package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import terrains.Terrain;
import utils.ICamera;

public class Camera implements ICamera {

	private static final float FOV = 60;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 300;

	private static final float ZOOM_COEF = 0.1f;
	private static final float PITCH_COEF = 0.15f;
	
	private static final float DISTANCE_FROM_PLAYER = 4;
	private static final float PITCH_THIRD_PERSON = 0;
	private static final float PITCH_FIRST_PERSON = -5;



	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();
	
	private Vector3f position = new Vector3f(0, 0, 0);

	private float pitch = PITCH_FIRST_PERSON;
	private float yaw = 0;
	private float roll;

	private float angleAroundPlayer = 0;
	private float distanceFromPlayer = 0;



	private Terrain terrain = null;
	private Player player;

	public enum Perspective {
		FIRST_PERSON,
		THIRD_PERSON,
	}
	
	private Perspective currentPerspective = Perspective.FIRST_PERSON;

	public Camera(Player player) {
		this.player = player;
	}

	public Camera(Player player, Terrain terrain) {
		this.player = player;
		this.terrain = terrain;
		this.projectionMatrix = createProjectionMatrix();
	}

	public void setPerspective(Perspective newPerspective) {
		currentPerspective = newPerspective;
	}

	private void updateViewMatrix() {
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix,
				viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x,-position.y,-position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	}

	private static Matrix4f createProjectionMatrix(){
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
	
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}

	public void move() {



		this.checkInputs();
		this.calculateZoom();
		this.calculatePitch();
		this.calculateAngleAroundPlayer();
		float horizontalDistance = this.calculateHorizontalDistance();
		float verticalDistance = this.calculateVerticalDistance();
		this.calculateCameraPosition(horizontalDistance, verticalDistance);
		// change camera angle to point towards the player
		this.yaw = 180 - (player.getRotY() + this.angleAroundPlayer);
		yaw %= 360;
		updateViewMatrix();
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

	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
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
				this.pitch = PITCH_FIRST_PERSON;
				this.player.setRenderingEnabled(false);
			} else if (this.currentPerspective == Camera.Perspective.FIRST_PERSON) {
				this.currentPerspective = Camera.Perspective.THIRD_PERSON;
				this.distanceFromPlayer = DISTANCE_FROM_PLAYER;
				this.pitch = PITCH_THIRD_PERSON;
				this.player.setRenderingEnabled(true);
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			// move left sidewise
		} else if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			// move right sidewise
		}
	}

	@Override
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public void reflect(float height){
		this.pitch = -pitch;
		this.position.y = position.y - 2 * (position.y - height);
		updateViewMatrix();
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4f getProjectionViewMatrix() {
		// TODO Auto-generated method stub
		return Matrix4f.mul(projectionMatrix, viewMatrix, null);
	}
}
