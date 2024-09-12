package photon.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import photon.utils.Maths;

public class Camera {
	
	private float distanceFromPlayer = 0;
	private float angleAroundPlayer = 0;
	private float eyeLevel = 2.0f;
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch;
	private float yaw;
	private float roll;
	
	private boolean staticCamera;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
		this.staticCamera = false;
	}
	
	public Camera() {
		this.player = null;
		this.staticCamera = true;
	}
	
	public void move() {
		if (!staticCamera) {
			calculateZoom();
			calculatePitch();
			calculateAngleAroundPlayer();
			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();
			calculateCameraPosition(horizontalDistance,verticalDistance);
			this.yaw = 180 - player.getRotY();//180 - (player.getRotY() + angleAroundPlayer);
		}
	}
	
	public void invertPitch() {
		this.pitch = -pitch;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = player.getRotY();// + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.y = player.getPosition().y + verticDistance + eyeLevel;
		position.z = player.getPosition().z - offsetZ;
	}
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch() {
		float pitchChange = Mouse.getDY() * 0.1f;
		pitch -= pitchChange;
		pitch = Maths.clamp(pitch, -45.0f, 45.0f);
	}
	
	private void calculateAngleAroundPlayer() {
		float angleChange = Mouse.getDX() * 0.3f;
		angleAroundPlayer -= angleChange;
		player.setRotY(angleAroundPlayer);
	}
	
}
