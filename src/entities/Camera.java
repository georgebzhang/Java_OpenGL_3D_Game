package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch; // how high or low camera is aimed
	private float yaw; // how left or right camera is aimed
	private float roll; // how tilted camera is
	
	public void move() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			position.z -= 0.02f;
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			position.x -= 0.02f;
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			position.x += 0.02f;
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
}
