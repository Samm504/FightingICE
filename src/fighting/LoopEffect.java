package fighting;

import image.Image;

/**
 * A class that manages the effects of the Hadouken attack.
 * It inherits from the Effect class.
 */
public class LoopEffect extends Effect {

	/**
	 * Constructor for creating an instance of LoopEffect with the specified data.
	 *
	 * @param attack The attack object
	 * @param hitImages All effect images corresponding to the attack object
	 */
	public LoopEffect(Attack attack, Image[] hitImages) {
		super(attack, hitImages);
	}

	/**
	 * Updates the effect's state.<br>
	 * If effect display time has elapsed, set the elapsed frame to 0;
	 *
	 * @return {@code true}
	 */
	public boolean update() {
		if (!super.update()) {
			this.currentFrame = 0;
		}

		return true;
	}

}
