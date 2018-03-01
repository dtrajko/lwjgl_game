package collision;

import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;

public class AABB {

	private Vector3f minExtents;
	private Vector3f maxExtents;

	public AABB(Vector3f minExtents, Vector3f maxExtents) {
		this.minExtents = minExtents;
		this.maxExtents = maxExtents;
	}
	
	public IntersectData intersectAABB(AABB other) {
		Vector3f distances1 = new Vector3f();
		Vector3f distances2 = new Vector3f();
		Vector3f distances = new Vector3f();
		Vector3f.sub(other.getMinExtents(), this.maxExtents, distances1);
		Vector3f.sub(this.minExtents, other.getMaxExtents(), distances2);
		distances = Maths.vectorMax3f(distances1, distances2);
		float maxDistance = Maths.vectorMaxComponent3f(distances);
		return new IntersectData(maxDistance <= 0, maxDistance);
	}

	public Vector3f getMinExtents() {
		return this.minExtents;
	}

	public Vector3f getMaxExtents() {
		return this.maxExtents;
	}
	
	public void setMinExtents(Vector3f minExtents) {
		this.minExtents = minExtents;
	}

	public void setMaxExtents(Vector3f maxExtents) {
		this.maxExtents = maxExtents;
	}
}
