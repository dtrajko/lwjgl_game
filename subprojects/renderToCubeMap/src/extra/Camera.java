package extra;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import utils.ICamera;
import utils.SmoothFloat;

public class Camera implements ICamera{
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 300;
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();
	
	private Vector3f position = new Vector3f(0, 0, 0);

	private float pitch = 10;

	private float yaw = 0;

	private float roll;

	private SmoothFloat angleAroundPlayer = new SmoothFloat(0, 10);
	private SmoothFloat distanceFromPlayer = new SmoothFloat(30, 5);

	public Camera(){
		this.projectionMatrix = createProjectionMatrix();
	}

	public void move(){
		calculatePitch();
		calculateAngleAroundPlayer();
		calculateZoom();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw =360- angleAroundPlayer.get();
		yaw%=360;
		updateViewMatrix();
	}

	@Override
	public Vector3f getPosition() {
		return position;
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
	
	public void invertPitch(){
		this.pitch = -pitch;
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

	private void calculateCameraPosition(float horizDistance, float verticDistance){
		float theta = angleAroundPlayer.get();
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = offsetX;
		position.z = offsetZ;
		position.y = verticDistance+2;
	}
	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer.get() * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer.get() * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculatePitch(){
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY() * 0.2f;
			pitch -= pitchChange;
			if(pitch < 0f){
				pitch = 0f;
			}else if(pitch > 90){
				pitch = 90;
			}
		}
	}
	
	private void calculateZoom(){
		float targetZoom = distanceFromPlayer.getTarget();
		float zoomLevel = Mouse.getDWheel() * 0.0008f * targetZoom;
		targetZoom -= zoomLevel;
		if(targetZoom<1){
			targetZoom = 1;
		}
		distanceFromPlayer.setTarget(targetZoom);
		distanceFromPlayer.update(0.01f);
	}
	
	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(0)){
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer.increaseTarget(-angleChange);;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			angleAroundPlayer.increaseTarget(0.5f);
		}
		angleAroundPlayer.update(0.01f);
	}
	
}
