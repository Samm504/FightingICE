package input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

import org.lwjgl.glfw.GLFWKeyCallback;

import enumerate.GameSceneName;
import manager.InputManager;

/**
 * A class for handling keyboard input.
 */
public class Keyboard extends GLFWKeyCallback {

	/**
	 * An array to store boolean values indicating whether each key is pressed.
	 */
	public static boolean[] keys = new boolean[65536];

	/**
	 * An array to store boolean values indicating whether each key was pressed in the previous frame.
	 */
	private static boolean[] preKeys = new boolean[65536];

	/**
	 * Class constructor.
	 */
	public Keyboard() {
		Arrays.fill(keys, false);
		Arrays.fill(preKeys, false);
	}

	// The GLFWKeyCallback class is an abstract method that
	// can't be instantiated by itself and must instead be extended
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key >= 0 && key <= 65536) {
			keys[key] = action != GLFW_RELEASE;
			if (action == GLFW_RELEASE) {
				preKeys[key] = false;
			}
		}
	}

	/**
	 * Returns whether the specified key is currently being pressed.
	 *
	 * @param keycode
	 *            The key to check
	 * @return {@code true} if the specified key is being pressed, {@code false} otherwise
	 */
	public static boolean getKey(int keycode) {
		return keys[keycode];
	}

	/**
	 * Returns whether the specified key is currently being pressed.
	 *
	 * @param keycode
	 *            The key to check
	 * @return In the game scene "PLAY":
	 *         <p>
	 *         {@code true} if the specified key is being pressed, or
	 *         <p>
	 *         {@code false} if the specified key is not being pressed.
	 *         <p>
	 *         In other game scenes:
	 *         <p>
	 *         {@code true} if the specified key is being pressed, or
	 *         <p>
	 *         {@code false} if the specified key is not being pressed or was pressed in the previous frame.
	 */
	public static boolean getKeyDown(int keycode) {
		if (InputManager.getInstance().getSceneName() == GameSceneName.PLAY) {
			if (!keys[keycode]) {
				return false;
			} else {
				preKeys[keycode] = true;
				return true;
			}
		} else {
			if (!keys[keycode] || preKeys[keycode]) {
				return false;
			} else {
				preKeys[keycode] = true;
				return true;
			}
		}
	}

	/**
	 * Cleanup operation.
	 */
	public void close() {

	}
}
