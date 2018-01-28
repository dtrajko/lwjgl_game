package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import collision.AABB;
import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {
	
	private static final float VERTICAL_OFFSET = -5;
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	private static final float TERRAIN_HEIGHT = 0;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	
	private boolean isInAir = false;
	
	private float speedCoeficient = 3;

	private boolean gravityEnabled = true;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		super.setAABB(new AABB(super.getPosition(), super.getPosition()));
	}

	public void move(Terrain terrain) {
		checkInputs();

		// prevent shaking when standing on objects
		float gravity = GRAVITY;
		if (gravityEnabled == false) {
			gravity = 0;
		}
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		upwardSpeed += gravity * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(dx, upwardSpeed * DisplayManager.getFrameTimeSeconds(), dz);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y < terrainHeight) {
			upwardSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
		// System.out.println("Gravity enabled? " + gravityEnabled + ", gravity amount = " + gravity);
	}

	public void increasePosition(float dx, float dy, float dz) {
		super.increasePosition(dx, dy, dz);
		// dtrajko: experimental code for AABB
		super.getAABB().setMinExtents(new Vector3f(super.getPosition().x - 2, super.getPosition().y - 2, super.getPosition().z - 2));
		super.getAABB().setMaxExtents(new Vector3f(super.getPosition().x + 2, super.getPosition().y + 2, super.getPosition().z + 2));
	}

	// prevent shaking when standing on objects
	public boolean isGravityEnabled() {
		return gravityEnabled;
	}

	// prevent shaking when standing on objects
	public void setGravityEnabled(boolean gravityEnabled) {
		this.gravityEnabled = gravityEnabled;
	}

	public void jump() {
		if (!isInAir) {
			this.upwardSpeed = JUMP_POWER;
			// isInAir = true;			
		}
	}
	
	public boolean isInAir() {
		return this.isInAir;
	}

	public void checkInputs() {

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			currentSpeed = RUN_SPEED * speedCoeficient;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			currentSpeed = -RUN_SPEED * speedCoeficient;
		} else {
			currentSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			currentTurnSpeed = TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentTurnSpeed = -TURN_SPEED;
		} else {
			currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			speedCoeficient = 5;
		} else {
			speedCoeficient = 3;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
			System.out.println("Player location: "
				+ " X = " + super.getPosition().x
				+ " Y = " + super.getPosition().y
				+ " Z = " + super.getPosition().z
			);
			/*
			System.out.println("Player AABB location:"
				+ " Min X = " + this.getAABB().getMinExtents().x
				+ " Max X = " + this.getAABB().getMaxExtents().x
				+ " Min Z = " + this.getAABB().getMinExtents().z
				+ " Max Z = " + this.getAABB().getMaxExtents().z
			);
			*/
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			// move left sidewise
		} else if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			// move right sidewise
		}
	}
	
	public float getVerticalOffset() {
		return this.VERTICAL_OFFSET;
	}
}
