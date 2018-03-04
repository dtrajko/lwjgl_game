package sunRenderer;

import org.lwjgl.util.vector.Vector3f;

import lensFlare.FlareManager;
import textures.Texture;
import utils.Light;

public class Sun {

	private static final float SUN_DIS = 50; // fairly arbitrary - but make sure it doesn't go behind skybox

	private final Texture texture;
	private float scale;
	private Light light;
	private FlareManager lensFlare = null;

	public Sun(Texture texture, float scale, Light light) {
		this.texture = texture;
		this.scale = scale;
		this.light = light;
		this.lensFlare = null;
	}

	public Sun(Texture texture, float scale, Light light, FlareManager lensFlare) {
		this.texture = texture;
		this.scale = scale;
		this.light = light;
		this.lensFlare = lensFlare;
	}

	public void delete(){
		if (lensFlare != null) {
			lensFlare.cleanUp();			
		}
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void setDirection(Vector3f dir) {
		light.setDirection(dir);
	}

	public Light getLight() {
		return this.light;
	}

	public FlareManager getLensFlare() {
		return this.lensFlare;
	}

	public Texture getTexture() {
		return texture;
	}

	public float getScale() {
		return scale;
	}

	/**
	 * Calculates a position for the sun, based on the light direction. The
	 * distance of the sun from the camera is fairly arbitrary, although care
	 * should be taken to ensure it doesn't get rendered outside the skybox.
	 * 
	 * @param camPos - The camera's position.
	 * @return The 3D world position of the sun.
	 */
	public Vector3f getWorldPosition(Vector3f camPos) {
		Vector3f sunPos = new Vector3f(this.light.getDirection());
		sunPos.negate();
		sunPos.scale(SUN_DIS);
		return Vector3f.add(camPos, sunPos, null);
	}
}
