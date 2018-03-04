package collision;

public class IntersectData {

	private boolean isIntersecting;
	private float maxDistance;

	public IntersectData(boolean isIntersecting, float maxDistance) {
		this.isIntersecting = isIntersecting;
		this.maxDistance = maxDistance;
	}

	public boolean isIntersecting() {
		return isIntersecting;
	}

	public float getMaxDistance() {
		return maxDistance;
	}
	
	
}
