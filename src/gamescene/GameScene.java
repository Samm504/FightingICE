package gamescene;

import enumerate.GameSceneName;

/**
 * Parent class that encapsulates common parts of game scenes such as Launcher and Play.
 */
public class GameScene {

	/*
	 * The name of the current game scene.
	 */
	protected GameSceneName gameSceneName;

	/**
	 * Flag indicating whether the game has ended.
	 */
	protected boolean isGameEndFlag;

	/**
	 * Flag indicating whether there is a request to transition to the next game scene.
	 */
	protected boolean isTransitionFlag;

	/**
	 * The next game scene to transition to.
	 */
	protected GameScene nextGameScene;

	/**
	 * Class constructor.
	 */
	public GameScene() {
		this.gameSceneName = GameSceneName.HOME_MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
	}

	/**
	 * Class constructor that generates a copy of the specified gameScene instance.
	 *
	 * @param gameScene
	 *            The specified game scene
	 */
	public GameScene(GameScene gameScene) {
		this.gameSceneName = gameScene.getCurrentSceneName();
		this.isGameEndFlag = gameScene.isGameEndFlag;
		this.isTransitionFlag = gameScene.isTransitionFlag;
		this.nextGameScene = gameScene.getNextGameScene();

	}

	/**
	 * Class constructor that creates an instance with the specified data.
	 *
	 * @param gameSceneName
	 *            The specified game scene name
	 * @param isEndFlag
	 *            Flag indicating whether the current scene has ended
	 * @param isTransitionFlag
	 *            Flag indicating whether there is a transition request
	 * @param nextGameScene
	 *            The next game scene to transition to
	 *
	 */
	public GameScene(GameSceneName gameSceneName, boolean isEndFlag, boolean isTransitionFlag,
			GameScene nextGameScene) {
		this.gameSceneName = gameSceneName;
		this.isGameEndFlag = isEndFlag;
		this.isTransitionFlag = isTransitionFlag;
		this.nextGameScene = nextGameScene;
	}

	/**
	 * Performs initialization for the current game scene.
	 */
	public void initialize() {
	}

	/**
	 * Performs an update for the current game scene.
	 */
	public void update() {
	}

	/**
	 * Performs closing operations for the current game scene.
	 */
	public void close() {
	}

	/**
	 * Gets the next game scene to transition to.
	 *
	 * @return The next game scene to transition to
	 */
	public GameScene getNextGameScene() {
		return nextGameScene;
	}

	/**
	 * Gets the name of the current game scene.
	 *
	 * @return The name of the current game scene
	 */
	public GameSceneName getCurrentSceneName() {
		return this.gameSceneName;
	}

	/**
	 * Sets the next game scene for transition.
	 *
	 * @param next
	 *            The next game scene for transition
	 */
	public void setNextGameScene(GameScene next) {
		this.nextGameScene = next;
	}

	/**
	 * Returns whether there is a request to end the game.
	 *
	 * @return {@code true} if there is a request to end the game; {@code false} otherwise
	 */
	public boolean isGameEnd() {
		return this.isGameEndFlag;
	}

	/**
	 * Returns whether there is a request to transition to the next game scene.
	 *
	 * @return {@code true} if there is a request to transition to the next game scene; {@code false} otherwise
	 */
	public boolean isTransition() {
		return this.isTransitionFlag;
	}

	/**
	 * Sets the flag indicating a request to end the game.<br>
	 * {@code true} if there is a request to end the game; {@code false} if there is no request.
	 *
	 * @param isEnd
	 *            Flag indicating whether there is a request to end the game
	 */
	public void setGameEndFlag(boolean isEnd) {
		this.isGameEndFlag = isEnd;
	}

	/**
	 * Sets the flag indicating a request to transition to the next game scene.<br>
	 * {@code true} if there is a request to transition; {@code false} if there is no request.
	 *
	 * @param isTransition
	 *            Flag indicating whether there is a request to transition to the next game scene
	 */
	public void setTransitionFlag(boolean isTransition) {
		this.isTransitionFlag = isTransition;
	}
}
