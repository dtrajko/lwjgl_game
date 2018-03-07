package extra;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import entities.Player;
import scene.ICamera;
import scene.Scene;

/**
 * Represents the in-game camera. This class is in charge of keeping the
 * projection-view-matrix updated. It allows the user to alter the pitch and yaw
 * with the left mouse button.
 * 
 * @author Karl
 */
public class Camera implements ICamera {

	private static final float PITCH_SENSITIVITY = 0.3f;
	private static final float YAW_SENSITIVITY = 0.3f;
	private static final float MAX_PITCH = 360;
	private static final float ZOOM_COEF = 0.01f;

	private static final float FOV = 60;
	private static final float NEAR_PLANE = 0.5f;
	private static final float FAR_PLANE = 1000;

	private static final float DISTANCE_FROM_PLAYER = 4f;
	private static final float PITCH_THIRD_PERSON = -10;
	private static final float PITCH_FIRST_PERSON = -10;

	private static final float Y_OFFSET = 2;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();

	private Vector3f position = new Vector3f(0, 0, 0);

	private float pitch = PITCH_THIRD_PERSON;
	private float yaw = 0;
	private float roll;

	private float angleAroundPlayer = 0;
	private float distanceFromPlayer = DISTANCE_FROM_PLAYER;
	
	// private Vector2f center = new Vector2f();

	private Scene scene;
	private Player player = null;

	private boolean reflected = false;

	private enum Perspective {
		FIRST_PERSON,
		THIRD_PERSON,
	}

	private Perspective currentPerspective = Perspective.THIRD_PERSON;

	private boolean terrainCollisionEnabled = false;

	public Camera() {
		this.projectionMatrix = createProjectionMatrix();
		System.out.println("extra.Camera object instantiated.");
	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
		this.player = scene.getAnimatedPlayer();
		System.out.println("Camera set the Scene and the Player");
	}

	private void updateViewMatrix() {
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix,
				viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x,-position.y,-position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	}

	private static Matrix4f createProjectionMatrix() {
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
		if (this.player == null) {
			this.player = scene.getAnimatedPlayer();
		}
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

	public void invertPitch(){
		this.pitch = -pitch;
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public float getPitch() {
		return pitch;
	}

	@Override
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	
	public float getAngleAroundPlayer() {
		return angleAroundPlayer;
	}

	public void setAngleAroundPlayer(float angleAroundPlayer) {
		this.angleAroundPlayer = angleAroundPlayer;
	}

	@Override
	public double getYaw() {
		return yaw;
	}

	@Override
	public double getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance + Y_OFFSET;

		if (terrainCollisionEnabled && player.getTerrain() != null) {
			if (player.getTerrain().getHeightOfTerrain(position.x, position.z) > position.y) {
				position.y = player.getTerrain().getHeightOfTerrain(position.x, position.z) + 5;
				pitch += 10f;
			}
		}
	}

	/**
	 * @return The horizontal distance of the camera from the origin.
	 */
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	/**
	 * @return The height of the camera from the aim point.
	 */
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * ZOOM_COEF;
		distanceFromPlayer -= zoomLevel;
	}

	/**
	 * Calculate the pitch and change the pitch if the user is moving the mouse
	 * up or down with the LMB pressed.
	 */
	private void calculatePitch(){
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY() * PITCH_SENSITIVITY;
			pitch -= pitchChange;
			clampPitch();
		}
	}

	/**
	 * Ensures the camera's pitch isn't too high or too low.
	 */
	private void clampPitch() {
		if (pitch < -MAX_PITCH){
			pitch = -MAX_PITCH;
		} else if(pitch > MAX_PITCH){
			pitch = MAX_PITCH;
		}
	}

	/**
	 * Calculate the angle of the camera around the player (when looking down at
	 * the camera from above). Basically the yaw. Changes the yaw when the user
	 * moves the mouse horizontally with the LMB down.
	 */
	private void calculateAngleAroundPlayer(){
		if (Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * YAW_SENSITIVITY;
			angleAroundPlayer -= angleChange;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
			angleAroundPlayer += 0.05f;
		}
	}

	public void togglePerspective() {
		if (this.currentPerspective == Camera.Perspective.THIRD_PERSON) {
			this.currentPerspective = Camera.Perspective.FIRST_PERSON;
			this.distanceFromPlayer = 0;
			this.pitch = Camera.PITCH_FIRST_PERSON;
			this.player.setRenderingEnabled(false);
		} else if (this.currentPerspective == Camera.Perspective.FIRST_PERSON) {
			this.currentPerspective = Camera.Perspective.THIRD_PERSON;
			this.distanceFromPlayer = Camera.DISTANCE_FROM_PLAYER;
			this.pitch = Camera.PITCH_THIRD_PERSON;
			this.player.setRenderingEnabled(true);
		}
	}

	public void checkInputs() {

		// camera perspective
		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			togglePerspective();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
			System.out.println("Player position: " 
				+ " X: " + player.getPosition().x 
				+ " Y: " + player.getPosition().y
				+ " Z: " + player.getPosition().z
			);
			System.out.println("Camera position: " 
					+ " X: " + position.x
					+ " Y: " + position.y
					+ " Z: " + position.z
					+ " distanceFromPlayer: " + distanceFromPlayer
				);
		}
	}

	@Override
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	@Override
	public void reflect(float height){
		invertPitch();
		this.position.y = position.y - 2 * (position.y - height);
		updateViewMatrix();
	}

	@Override
	public void reflect(){
		this.reflected = !reflected;
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4f getProjectionViewMatrix() {
		return Matrix4f.mul(projectionMatrix, viewMatrix, null);
	}

	@Override
	public void switchToFace(int faceIndex) {
		switch (faceIndex) {
		case 0:
			pitch = 0;
			yaw = 90;
			break;
		case 1:
			pitch = 0;
			yaw = -90;
			break;
		case 2:
			pitch = -90;
			yaw = 180;
			break;
		case 3:
			pitch = 90;
			yaw = 180;
			break;
		case 4:
			pitch = 0;
			yaw = 180;
			break;
		case 5:
			pitch = 0;
			yaw = 0;
			break;
		}
		updateViewMatrix();
	}

	@Override
	public float getNearPlane() {
		return NEAR_PLANE;
	}

	@Override
	public float getFarPlane() {
		return FAR_PLANE;
	}
}
