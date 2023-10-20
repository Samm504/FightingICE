package input;

import struct.Key;

/**
 * A class for managing the Key input from each player in the same frame.
 */
public class KeyData {

	/**
	 * An array to store the input Keys for P1 and P2.
	 * Index 0 is for P1, and index 1 is for P2.
	 */
	private Key[] keys;

	/**
	 * Class constructor.
	 */
	public KeyData() {
		this.keys = new Key[2];
		for (int i = 0; i < this.keys.length; i++) {
			this.keys[i] = new Key();
		}
	}

	/**
	 * Class constructor to create a KeyData instance from the input Keys of each player.
	 *
	 * @param keys
	 *            An array containing the input Keys for P1 and P2.
	 *            Index 0 is for P1, and index 1 is for P2.
	 */
	public KeyData(Key[] keys) {
		this.keys = new Key[keys.length];
		for (int i = 0; i < keys.length; i++) {
			this.keys[i] = new Key(keys[i]);
		}
	}

	/**
	 * Class constructor to generate a copy of the provided KeyData instance.
	 * If the argument is null, it initializes the Key array and creates a new instance.
	 *
	 * @param keyData
	 *            Input key information for P1 and P2.
	 *            Index 0 is for P1, and index 1 is for P2.
	 */
	public KeyData(KeyData keyData) {
		if (keyData != null) {
			this.keys = new Key[keyData.getKeys().length];
			for (int i = 0; i < keyData.getKeys().length; i++) {
				this.keys[i] = new Key(keyData.getKeys()[i]);
			}
		} else {
			this.keys = new Key[2];
			for (int i = 0; i < this.keys.length; i++) {
				this.keys[i] = new Key();
			}
		}
	}

	/**
	 * Get an array containing the input Keys for P1 and P2.
	 *
	 * @return An array containing the input Keys for P1 and P2.
	 *         Index 0 is for P1, and index 1 is for P2.
	 */
	public Key[] getKeys() {
		return this.keys.clone();
	}
}
