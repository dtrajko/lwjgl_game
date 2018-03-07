package entities;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import animatedModel.AnimatedModel;
import animatedModel.Joint;
import extra.Camera;
import input.GamepadManager;
import loaders.SceneLoader;
import openglObjects.Vao;
import scene.ICamera;
import terrains.Terrain;
import textures.Texture;
import utils.DisplayManager;

public class Player extends AnimatedModel {

	private static final float VERTICAL_OFFSET = -5;
	private static final float RUN_SPEED = 10;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -20;
	private static final float JUMP_POWER = 10;
	private static final float HEIGHT = 5.5f;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	
	private boolean isInAir = false;
	private boolean gravityEnabled = true;
	
	private Terrain terrain = null;
	
	private boolean generateParticles = false;

	public Player(Vao model, Texture texture, Joint rootJoint, int jointCount, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, texture, rootJoint, jointCount, position, rotX, rotY, rotZ, scale);
	}

	public void update(Terrain terrain) {
		this.terrain = terrain;
		move(terrain);
		super.update(getCurrentSpeed());
	}

	public void move(Terrain terrain) {
		checkInputs();

		// prevent shaking when standing on objects
		float gravity = GRAVITY;
		if (gravityEnabled == false) {
			gravity = 0;
		}
		float rotY = currentTurnSpeed * DisplayManager.getFrameTimeSeconds();
		super.increaseRotation(0, rotY, 0);
		float distance = getCurrentSpeed() * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		upwardSpeed += gravity * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(dx, upwardSpeed * DisplayManager.getFrameTimeSeconds(), dz);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y <= terrainHeight) {
			upwardSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
		DisplayManager.setTitle(DisplayManager.getTitle() + " | FPS=" + DisplayManager.getFPS() +
			" | PosX= " + Math.round(this.getPosition().x) + " PoxY= " + Math.round(this.getPosition().y) + " PosZ=" + Math.round(this.getPosition().z) +
			" | RotX= " + Math.round(this.getRotX()) + " RotY=" + Math.round(this.getRotY()) + " RotZ=" + Math.round(this.getRotZ()) +
			" | Speed=" + getCurrentSpeed());
	}

	public static float getHeight() {
		return HEIGHT;
	}

	public static float getGravity() {
		return GRAVITY;
	}
	
	public Terrain getTerrain() {
		return this.terrain;
	}

	public float getVerticalOffset() {
		return Player.VERTICAL_OFFSET;
	}

	public float getRunSpeed() {
		return Player.RUN_SPEED;
	}

	public float getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(float currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public float getTurnSpeed() {
		return Player.TURN_SPEED;
	}

	public float getCurrentTurnSpeed() {
		return this.currentTurnSpeed;
	}

	public void setCurrentTurnSpeed(float currentTurnSpeed) {
		this.currentTurnSpeed = currentTurnSpeed;
	}

	public void increasePosition(float dx, float dy, float dz) {
		super.increasePosition(dx, dy, dz);
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

		setCurrentSpeed(0);
		if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			setCurrentSpeed(RUN_SPEED);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			setCurrentSpeed(-RUN_SPEED);
		}

		currentTurnSpeed = 0;
		if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			currentTurnSpeed = TURN_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			currentTurnSpeed = -TURN_SPEED;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}

		GamepadManager.handleInput();

		if (generateParticles && Mouse.isButtonDown(2)) { // 2 for mouse wheel button
			SceneLoader.getScene().getParticleSystems().get(0).generateParticles(new Vector3f(
				this.getPosition().getX(),
				this.getPosition().getY() + 0.1f,
				this.getPosition().getZ()
			));
			System.out.println("Generating particles");
		}
	}
}
