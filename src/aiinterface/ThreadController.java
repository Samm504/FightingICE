package aiinterface;

/**
 * Class that handles thread-related processes such as AI execution timing.
 */
public class ThreadController {

	/**
     * The sole instance of ThreadController.
     */
	private static ThreadController threadController = new ThreadController();

	/**
     * Object managing the start timing of P1's AI processing.
     */
	private Object AI1;

	/**
     * Object managing the start timing of P2's AI processing.
     */
	private Object AI2;

	/**
     * Flag indicating whether P1's AI processing is complete.<br>
     * Used only in Fastmode.
     */
	private boolean processedAI1;

	/**
     * Flag indicating whether P2's AI processing is complete.<br>
     * Used only in Fastmode.
     */
	private boolean processedAI2;

	/**
     * Synchronization object for starting each AI process simultaneously.
     */
	private Object endFrame;

	/**
     * Class constructor to initialize field variables.
     */
	private ThreadController() {
		this.AI1 = new Object();
		this.AI2 = new Object();
		this.endFrame = new Object();

		resetProcessedFlag();
	}

	/**
     * Get the sole instance of the ThreadController class.
     *
     * @return The sole instance of the ThreadController class
     */
	public static ThreadController getInstance() {
		return threadController;
	}

	/**
     * Resume processing for each AI.
     */
	public void resetAllAIsObj() {
		synchronized (this.AI1) {
			this.AI1.notifyAll();
		}
		synchronized (this.AI2) {
			this.AI2.notifyAll();
		}
	}

	/**
     * Return the synchronization object for the specified character.
     *
     * @param playerNumber
     *            The character's side flag: {@code true} if P1, {@code false} if P2.
     *
     * @return The synchronization object for the specified character
     */
	public Object getAIsObject(boolean playerNumber) {
		if (playerNumber)
			return this.AI1;
		else
			return this.AI2;
	}

	/**
     * Get the object indicating the completion of one frame of game processing.
     *
     * @return The object indicating the completion of one frame of game processing.
     */
	public Object getEndFrame() {
		return this.endFrame;
	}

	/**
     * Reset the flag indicating whether each AI's processing is complete to {@code false}.<br>
     * Used only in Fastmode.
     */
	private void resetProcessedFlag() {
		this.processedAI1 = false;
		this.processedAI2 = false;
	}

	/**
     * Set the flag indicating the completion of one frame of processing for the specified character.<br>
     * After setting, check whether both AIs have finished processing.<br>
     * Used only in Fastmode.
     *
     * @param playerNumber
     *            The character's side flag: {@code true} if P1, {@code false} if P2.
     */
	synchronized public void notifyEndProcess(boolean playerNumber) {
		if (playerNumber) {
			this.processedAI1 = true;
		} else {
			this.processedAI2 = true;
		}
		this.checkEndFrame();
	}

	/**
     * Check whether both AIs have finished processing for the current frame.<br>
     * If both have finished, start processing for the next frame.<br>
     * Used only in Fastmode.
     */
	private void checkEndFrame() {
		if (this.processedAI1 && this.processedAI2) {
			synchronized (this.endFrame) {
				this.endFrame.notifyAll();
			}
			this.processedAI1 = false;
			this.processedAI2 = false;
		}
	}

}
