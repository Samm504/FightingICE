package fighting;

import image.Image;

/**
 * A class that manages the effects displayed when an attack hits and upper attack effects.
 * It inherits from the Effect class.
 */
public class HitEffect extends Effect {

	/**
	 * The boolean value whether the attack conducted by the character hits the
	 * opponent or not.
	 */
	private boolean isHit;

	/**
	 * The horizontal variation of the display position of this effect.
	 */
	private int variationX;

	/**
	 * The vertical variation of the display position of this effect.
	 */
	private int variationY;

	/**
	 * Constructor for creating an instance of HitEffect with the specified data.
	 *
	 * @param attack The attack object
	 * @param hitImages All effect images corresponding to the attack object
	 * @param isHit The boolean value indicating whether the attack conducted by the character hits the opponent or not
	 *
	 * @param variation Whether to vary the display position of the effect images based on variationX and variationY
	 * @param framesPerImage The number of frames to display each effect image
	 */
	public HitEffect(Attack attack, Image[] hitImages, boolean isHit, boolean variation, int framesPerImage) {
		super(attack, hitImages, framesPerImage);
		this.initialize(isHit, variation);
	}

	/**
	 * Constructor for creating an instance of HitEffect with the specified data.
	 *
	 * @param attack The attack object
	 * @param hitImages All effect images corresponding to the attack object
	 * @param isHit The boolean value indicating whether the attack conducted by the character hits the opponent or not
	 *
	 * @param variation Whether to vary the display position of the effect images based on variationX and variationY
	 */         エフェクト画像の表示位置をvariationX, variationYに従って変動させるかどうか
	 */
	public HitEffect(Attack attack, Image[] hitImages, boolean isHit, boolean variation) {
		super(attack, hitImages);
		this.initialize(isHit, variation);
	}

	/**
	 * Constructor for creating an instance of HitEffect with the specified data.
	 *
	 * @param attack The attack object
	 * @param hitImages All effect images corresponding to the attack object
	 * @param isHit The boolean value indicating whether the attack conducted by the character hits the opponent or not
	 */
	public HitEffect(Attack attack, Image[] hitImages, boolean isHit) {
		this(attack, hitImages, isHit, true);
	}

	/**
	 * Initializes the hit effect.
	 *
	 * @param isHit The boolean value indicating whether the attack conducted by the character hits the opponent or not
	 * @param variation Whether to vary the display position of the effect images based on variationX and variationY
	 */
	private void initialize(boolean isHit, boolean variation) {
		this.isHit = isHit;
		this.variationX = variation ? (int) (Math.random() * 30) - 15 : 0;
		this.variationY = variation ? (int) (Math.random() * 30) - 15 : 0;
	}

	/**
	 * Returns the boolean value whether the attack conducted by the character
	 * hits the opponent or not.
	 *
	 * @return the boolean value whether the attack conducted by the character
	 *         hits the opponent or not
	 */
	public boolean isHit() {
		return this.isHit;
	}

	/**
	 * Returns the horizontal variation of the display position of this effect.
	 *
	 * @return the horizontal variation of the display position of this effect
	 */
	public int getVariationX() {
		return this.variationX;
	}

	/**
	 * Returns the vertical variation of the display position of this effect.
	 *
	 * @return the vertical variation of the display position of this effect
	 */
	public int getVariationY() {
		return this.variationY;
	}

}
