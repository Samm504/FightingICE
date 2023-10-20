package informationcontainer;

import setting.FlagSetting;
import struct.FrameData;

/**
 * Class for processing round results.
 */
public class RoundResult {

	/**
	 * Current round number.
	 */
	private int currentRound;

	/**
	 * Array to store the remaining HP of P1 and P2.
	 */
	private int[] remainingHPs;

	/**
	 * Number of frames elapsed in the round.
	 */
	private int elapsedFrame;

	/**
	 * Class constructor.
	 */
	public RoundResult() {
		this.currentRound = -1;
		this.remainingHPs = new int[2];
		this.elapsedFrame = -1;
	}

	/**
	 * Class constructor to update RoundResult with specified values.
	 *
	 * @param round
	 *            Round number
	 * @param hp
	 *            Remaining HP of P1 and P2
	 * @param frame
	 *            Elapsed frame count
	 */
	public RoundResult(int round, int[] hp, int frame) {
		this.currentRound = round;
		this.remainingHPs = hp;
		this.elapsedFrame = frame;
	}

	/**
	 * Class constructor to update RoundResult with information related to results obtained from the provided frame data.
	 *
	 * @param frameData
	 *            Game data within the frame
	 */
	public RoundResult(FrameData frameData) {
		this.currentRound = frameData.getRound();
		this.elapsedFrame = frameData.getFramesNumber() + 1;
		this.remainingHPs = new int[] { frameData.getCharacter(true).getHp(), frameData.getCharacter(false).getHp() };

		if (FlagSetting.limitHpFlag) {
			this.remainingHPs[0] = Math.max(this.remainingHPs[0], 0);
			this.remainingHPs[1] = Math max(this.remainingHPs[1], 0);
		}
	}

	/**
	 * Get the current round number.
	 *
	 * @return Current round number
	 */
	public int getRound() {
		return this.currentRound;
	}

	/**
	 * Get an array containing the remaining HP of P1 and P2.
	 *
	 * @return Array containing the remaining HP of P1 and P2
	 */
	public int[] getRemainingHPs() {
		return this.remainingHPs.clone();
	}

	/**
	 * Get the number of elapsed frames.
	 *
	 * @return Number of elapsed frames
	 */
	public int getElapsedFrame() {
		return this.elapsedFrame;
	}
}
