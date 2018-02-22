package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private static final float ZOOM_COEF = 0.01f;

	private float distanceFromPlayer = 7;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 10;
	private float yaw = 0;
	private float roll;
	
	public Camera(){
	}
	
	public void move() {
		this.calculateZoom();
		this.calculatePitch();
		this.calculateAngleAroundPlayer();
		this.checkInputs();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw =360- angleAroundPlayer;
		yaw%=360;
	}

	public void invertPitch(){
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
	
	private void calculateCameraPosition(float horizDistance, float verticDistance){
		float theta = angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = offsetX;
		position.z = offsetZ;
		position.y = verticDistance + 2;
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
			if(pitch < 4){
				// pitch = 4;
			}else if(pitch > 90){
				// pitch = 90;
			}
		}
	}

	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(0)){
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			angleAroundPlayer+= 0.5f;
		}
	}

	public void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			pitch -= 0.5f;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			pitch += 0.5f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			angleAroundPlayer += 0.5f;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			angleAroundPlayer -= 0.5f;
		}
	}
}
