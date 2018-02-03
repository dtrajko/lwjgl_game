package textures;

public class ModelTexture {

	private int textureID;
	private int normalMap;

	private float shineDamper = 0;
	private float reflectivity = 0;

	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	private int numberOfRows = 1;

	public ModelTexture(int id) {
		this.textureID = id;
	}
	
	public int getNumberOfRows() {
		return numberOfRows;
	}
	
	public int getNormalMap() {
		return this.normalMap;
	}

	public ModelTexture setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
		return this;
	}

	public int getID() {
		return this.textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public ModelTexture setNormalMap(int normalMap) {
		this.normalMap = normalMap;
		return this;
	}

	public ModelTexture setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
		return this;
	}

	public ModelTexture setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
		return this;
	}

	public ModelTexture setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
		return this;
	}

	public ModelTexture setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
		return this;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public boolean hasTransparency() {
		return hasTransparency;
	}

	public boolean useFakeLighting() {
		return useFakeLighting;
	}
}
