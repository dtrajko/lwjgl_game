package extra;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import entities.Player;
import scene.ICamera;
import scene.Scene;
import utils.SmoothFloat;

public class Camera implements ICamera {
	
	private Scene scene;

	private static final float FOV = 60;
	private static final float NEAR_PLANE = 0.5f;
	private static final float FAR_PLANE = 1000;

	private static final float ZOOM_COEF = 0.1f;
	// private static final float PITCH_COEF = 0.15f;

	private static final float DISTANCE_FROM_PLAYER = 20;
	private static final float PITCH_THIRD_PERSON = 10;
	private static final float PITCH_FIRST_PERSON = 10;

	private static final float Y_OFFSET = 5;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();

	private Vector3f position = new Vector3f(0, 20, 10);

	private float pitch = PITCH_THIRD_PERSON;
	private float yaw = 0;
	private float roll;

	private float angleAroundPlayer = 180;
	private float distanceFromPlayer = DISTANCE_FROM_PLAYER;
	
	// private Vector2f center = new Vector2f();
	
	private Player player;


	private enum Perspective {
		FIRST_PERSON,
		THIRD_PERSON,
	}

	private Perspective currentPerspective = Perspective.THIRD_PERSON;

	public Camera() {
		this.projectionMatrix = createProjectionMatrix();
		System.out.println("extra.Camera object instantiated.");
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
		
		this.player = scene.getAnimatedPlayer();

		// checkInputs();
		movePosition();
		calculatePitch();
		calculateAngleAroundPlayer();
		calculateZoom();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw =360 - angleAroundPlayer;
		yaw %= 360;
		updateViewMatrix();
		
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

	private void movePosition() {
		position.x = player.getPosition().x;
		position.y = player.getPosition().y;
		position.z = player.getPosition().z;
		
	}

	public void invertPitch(){
		this.pitch = -pitch;
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public double getPitch() {
		return pitch;
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
		// position.y = verticDistance + Y_OFFSET;
		position.y = player.getPosition().y + verticDistance + Y_OFFSET;
	}

	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * ZOOM_COEF;
		distanceFromPlayer -= zoomLevel;
	}

	private void calculatePitch(){
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY() * 0.2f;
			pitch -= pitchChange;
			if(pitch < -90f){
				pitch = -90f;
			}else if(pitch > 90){
				pitch = 90;
			}
		}
	}

	private void calculateAngleAroundPlayer(){
		if (Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
			angleAroundPlayer += 0.05f;
		}
	}

	public void checkInputs() {

		// camera perspective
		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
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

		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			// TODO move left sidewise
		} else if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			// TODO move right sidewise
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
	public void setScene(Scene scene) {
		this.scene = scene;
		this.player = scene.getAnimatedPlayer();
	}
}
