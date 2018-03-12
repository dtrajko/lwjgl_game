package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import animatedModel.AnimatedModel;
import animatedModel.Joint;
import audio.AudioMaster;
import audio.Source;
import input.GamepadManager;
import interfaces.ITerrain;
import loaders.SceneLoader;
import main.WorldSettings;
import openglObjects.Vao;
import racetrack.LapStopwatch;
import terrains.Terrain;
import textures.Texture;
import utils.DisplayManager;

public class Player extends AnimatedModel {

	private static float VERTICAL_OFFSET = -5;
	private static float RUN_SPEED = 10;
	private static float TURN_SPEED = 160;
	private static float GRAVITY = -20;
	private static float JUMP_POWER = 10;
	private static float TERRAIN_HEIGHT = 0;
	private static float HEIGHT = 5.5f;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	
	private boolean isInAir = false;
	private boolean gravityEnabled = true;
	
	private ITerrain terrain = null;
	
	private boolean generateParticles = false;
	
	private Integer soundCartEngine;
	private Integer soundTyreScreech;
	private Source audioSourceEngine = null;
	private Source audioSourceTyres = null;

	public Player(Vao model, Texture texture, Joint rootJoint, int jointCount, Vector3f position, Vector3f rotation, float scale) {
		super(model, texture, rootJoint, jointCount, position, rotation.getX(), rotation.getY(), rotation.getZ(), scale);
		initSound();
	}

	public void setProperties() {
		// setings for the autokomerc karting game
		RUN_SPEED = 16;
		TURN_SPEED = 57;
	}

	public void update(ITerrain terrain) {
		this.terrain = terrain;
		move(terrain);
		handleSound();
		super.update(getCurrentSpeed());
	}

	private void initSound() {
		// init cart engine sound
		soundCartEngine = AudioMaster.loadSound("audio/cart_engine.wav");
		audioSourceEngine = new Source();
		audioSourceEngine.setVolume(0.2f);
		audioSourceEngine.setLooping(true);
		audioSourceEngine.play(soundCartEngine);
		// init tyre screetch sound
		soundTyreScreech = AudioMaster.loadSound("audio/tyre_screech.wav");
		audioSourceTyres = new Source();
		audioSourceTyres.setVolume(0.5f);
		audioSourceTyres.setLooping(true);
		audioSourceTyres.play(soundTyreScreech);
		audioSourceTyres.pause();	
	}

	private void handleSound() {
		if (currentTurnSpeed < -10 || currentTurnSpeed > 10) {
			if (!audioSourceTyres.isPlaying()) {
				audioSourceTyres.continuePlaying();
			}
		} else {
			if (audioSourceTyres.isPlaying()) {
				audioSourceTyres.pause();
			}
		}
	}

	public void move(ITerrain terrain) {

		checkInputs();

		if (super.getPosition().y > TERRAIN_HEIGHT + 0.5f) {
			currentSpeed /= 10f;
			SceneLoader.getScene().getRacetrack().getStopwatch().addPenaltySeconds(2);
		}

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

		// terrain collision detection
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y <= terrainHeight) {
			upwardSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}

		// prevent player going above the water level
		if (super.getPosition().y > WorldSettings.WATER_HEIGHT) {
			// dx = -dx;
			// dz = -dz;
		}
	}

	public static float getHeight() {
		return HEIGHT;
	}

	public static float getGravity() {
		return GRAVITY;
	}
	
	public ITerrain getTerrain() {
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
	
	public void cleanUp() {
		if (audioSourceEngine != null) {
			audioSourceEngine.delete();			
		}
		if (audioSourceTyres != null) {
			audioSourceTyres.delete();			
		}
		super.delete();
	}
}
