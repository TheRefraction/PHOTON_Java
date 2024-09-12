package photon.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import photon.models.TexturedModel;
import photon.system.DisplayManager;
import photon.terrains.Terrain;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 20;
	public static final float GRAVITY = -50;
	private static final float WATER_GRAVITY = -20;
	private static final float JUMP_POWER = 30;
	private static final float SWIM_POWER = 15;
	
	private Terrain currentTerrain;
	private int currentTerrainId = -1;
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private float angleOffset = 0;
	private boolean isInAir = false;
	private boolean isInWater = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void move(Terrain terrain) {
		checkInputs();
		currentTerrain = terrain;
		currentTerrainId = currentTerrain.getId();
		if (super.getPosition().y <= 0 - 2.0f) {
			isInWater = true;
		} else {
			isInWater = false;
		}
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY() + angleOffset)));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY() + angleOffset)));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += isInWater ? WATER_GRAVITY * DisplayManager.getFrameTimeSeconds() : GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0,upwardsSpeed * DisplayManager.getFrameTimeSeconds(),0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}
	
	private void jump() {
		if (!isInAir || isInWater) {
			upwardsSpeed = isInWater ? SWIM_POWER : JUMP_POWER;
			isInAir = true;
		}
	}
	
	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				angleOffset = 45;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				angleOffset = -45;
			} else {
				angleOffset = 0;
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				angleOffset = -45;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				angleOffset = 45;
			} else {
				angleOffset = 0;
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentSpeed = RUN_SPEED;
			angleOffset = 90;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentSpeed = RUN_SPEED;
			angleOffset = -90;
		} else {
			angleOffset = 0;
			this.currentSpeed = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && (!isInAir || isInWater)) {
			jump();
		}
	}

	public Terrain getTerrain() {
		return currentTerrain;
	}

	public int getTerrainId() {
		return currentTerrainId;
	}

	public boolean isInAir() {
		return isInAir;
	}

	public boolean isInWater() {
		return isInWater;
	}

}
