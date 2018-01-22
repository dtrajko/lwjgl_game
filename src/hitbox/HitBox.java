package hitbox;

import org.lwjgl.util.vector.Vector3f;

public class HitBox {

	public Vector3f[] corners = new Vector3f[8];

	public float xMin;
	public float xMax;

	public float yMin;
	public float yMax;

	public float zMin;
	public float zMax;

	public Vector3f cornerMax;
	public Vector3f cornerMin;

	private Vector3f position;
	private Vector3f rotation;

	public HitBox(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
		cornerMin = new Vector3f(xMin, yMin, zMin);
		cornerMax = new Vector3f(xMax, yMax, zMax);
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		generateCorners(xMin, xMax, yMin, yMax, zMin, zMax);
	}

	private void generateCorners(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {

		Vector3f c1 = new Vector3f(xMin, yMin, zMin);
		Vector3f c2 = new Vector3f(xMin, yMax, zMin);

		Vector3f c3 = new Vector3f(xMax, yMin, zMin);
		Vector3f c4 = new Vector3f(xMax, yMax, zMin);

		Vector3f c5 = new Vector3f(xMin, yMin, zMax);
		Vector3f c6 = new Vector3f(xMin, yMax, zMax);

		Vector3f c7 = new Vector3f(xMax, yMin, zMax);
		Vector3f c8 = new Vector3f(xMax, yMax, zMax);

		corners[0] = c1;
		corners[1] = c2;
		corners[2] = c3;
		corners[3] = c4;
		corners[4] = c5;
		corners[5] = c6;
		corners[6] = c7;
		corners[7] = c8;
	}

	public void setPosition(Vector3f pos) {
		position = pos;
	}

	public Vector3f getPosition() {
		return this.position;
	}

	public void setRotation(Vector3f rot) {
		rotation = rot;
	}

	public Vector3f getRotation() {
		return this.rotation;
	}

}