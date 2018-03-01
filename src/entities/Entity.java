package entities;

import org.lwjgl.util.vector.Vector3f;

import collision.AABB;

public class Entity {

	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private AABB aabb;
	
	private boolean renderingEnabled = true;

	public Entity(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public Entity(int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public Entity() {
		this.position = new Vector3f(0, 0, 0);
		this.rotX = 0;
		this.rotY = 0;
		this.rotZ = 0;
		this.scale = 1;
	}

	public boolean isRenderingEnabled() {
		return renderingEnabled;
	}

	public void setRenderingEnabled(boolean renderingEnabled) {
		this.renderingEnabled = renderingEnabled;
	}

	public void setAABB(AABB aabb) {
		this.aabb = aabb;
	}

	public AABB getAABB() {
		return	aabb;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	
}
