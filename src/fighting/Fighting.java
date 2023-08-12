package fighting;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import command.CommandTable;
import enumerate.Action;
import enumerate.State;
import image.Image;
import input.KeyData;
import manager.GraphicManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.AttackData;
import struct.CharacterData;
import struct.FrameData;
import struct.Key;

/**
 * Class that handles combat processing and updates of attack and character parameters.
 */
public class Fighting {

	/**
	 * The character's data of both characters<br>
	 * Index 0 is P1, index 1 is P2.
	 *
	 * @see Character
	 */
	protected Character[] playerCharacters;

	/**
	 * The list of projectile data of both characters.
	 *
	 * @see LoopEffect
	 */
	protected Deque<LoopEffect> projectileDeque;

	/**
	 * The list of the input information of both characters.
	 *
	 * @see KeyData
	 */
	private Deque<KeyData> inputCommands;

	/**
     * List to store hit effects and upper effects when an attack hits or is an upper attack.
     * Index 0 is P1, index 1 is P2.
     *
     * @see HitEffect
     */
	private LinkedList<LinkedList<HitEffect>> hitEffects;

	/**
     * Class variable that manages key input and its corresponding actions.
     *
     * @see CommandTable
     */
	protected CommandTable commandTable;


	/**
	 * Class constructor．
	 */
	public Fighting() {
		this.playerCharacters = new Character[2];
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.inputCommands = new LinkedList<KeyData>();
		this.commandTable = new CommandTable();
		this.hitEffects = new LinkedList<LinkedList<HitEffect>>();

	}
	
	public void processingRoundEnd(){
		this.inputCommands.clear();
		this.playerCharacters[0].setProcessedCommand(new LinkedList<Key>());
		this.playerCharacters[1].setProcessedCommand(new LinkedList<Key>());
		this.playerCharacters[0].setInputCommand(new LinkedList<Key>());
		this.playerCharacters[1].setInputCommand(new LinkedList<Key>());
		this.playerCharacters[0].resetEnergyCount();
		this.playerCharacters[1].resetEnergyCount();
	}
	
	/**
     * Initializes P1 and P2 character information and effect lists.
     */
	public void initialize() {
		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i] = new Character();
			this.playerCharacters[i].initialize(LaunchSetting.characterNames[i], i == 0);
			this.hitEffects.add(new LinkedList<HitEffect>());
		}
	}

	/**
     * Processes one frame of combat for P1 and P2 based on key input.
     *
     * @param currentFrame Current frame
     * @param keyData      Key input for P1 and P2. Index 0 is P1, index 1 is P2.
     */
	public void processingFight(int currentFrame, KeyData keyData) {

		// 1. Execute actions based on key input
		processingCommands(currentFrame, keyData);
		// 2. Handle hit detection and update character parameters
		calculationHit(currentFrame);
		// 3. Update attack parameters
		updateAttackParameter();
		// 4. Update character states
		updateCharacter();

	}

	/**
     * Executes actions based on key input.
     *
     * @param currentFrame Current frame
     * @param keyData      Key input for P1 and P2. Index 0 is P1, index 1 is P2.
     */
	protected void processingCommands(int currentFrame, KeyData keyData) {
		this.inputCommands.addLast(keyData);

		// Remove oldest data if the list size exceeds the limit (INPUT_LIMIT)
		while (this.inputCommands.size() > GameSetting.INPUT_LIMIT) {
			this.inputCommands.removeFirst();
		}

		// Execute actions
		for (int i = 0; i < 2; i++) {
			if (!this.inputCommands.isEmpty()) {
				Action executeAction = this.commandTable.interpretationCommandFromKeyData(this.playerCharacters[i],
						this.inputCommands);
				if (ableAction(this.playerCharacters[i], executeAction)) {
					this.playerCharacters[i].runAction(executeAction, true);
				}
			}
		}
	}

	/**
     * Handles hit detection, updates character parameters, and applies effects.
     *
     * @param currentFrame Current frame
     */
	protected void calculationHit(int currentFrame) {
		boolean[] isHit = { false, false };

		// Process projectiles
		int dequeSize = this.projectileDeque.size();
		for (int i = 0; i < dequeSize; i++) {
			LoopEffect projectile = this.projectileDeque.removeFirst();
			int opponentIndex = projectile.getAttack().isPlayerNumber() ? 1 : 0;

			if (detectionHit(this.playerCharacters[opponentIndex], projectile.getAttack())) {
				int myIndex = opponentIndex == 0 ? 1 : 0;
				this.playerCharacters[opponentIndex].hitAttack(this.playerCharacters[myIndex], projectile.getAttack(),
						currentFrame);
			} else {
				this.projectileDeque.addLast(projectile);
			}
		}

		// Process normal attacks
		for (int i = 0; i < 2; i++) {
			int opponentIndex = i == 0 ? 1 : 0;
			Attack attack = this.playerCharacters[i].getAttack();

			if (detectionHit(this.playerCharacters[opponentIndex], attack)) {
				isHit[i] = true;
				// HP等のパラメータの更新
				this.playerCharacters[opponentIndex].hitAttack(this.playerCharacters[i], attack, currentFrame);
			}
		}

		// Process effects (e.g., hit effects, upper effects)
		for (int i = 0; i < 2; i++) {
			if (FlagSetting.enableWindow) {
				if (this.playerCharacters[i].getAttack() != null) {
					// Set effect based on the current combo
					int comboState = Math.max(this.playerCharacters[i].getHitCount() - 1, 0);
					// Limit effect to 4th hit if combo is 4 or more
					comboState = Math.min(comboState, 3);

					Image[] effect = GraphicManager.getInstance().getHitEffectImageContaier()[comboState];
					this.hitEffects.get(i).add(new HitEffect(this.playerCharacters[i].getAttack(), effect, isHit[i]));

					// Process upper attacks
					if (playerCharacters[i].getAction() == Action.STAND_F_D_DFB) {
						Image[] upper = GraphicManager.getInstance().getUpperImageContainer()[i];
						Motion motion = this.playerCharacters[i].getMotionList().get(Action.STAND_F_D_DFB.ordinal());

						if (this.playerCharacters[i].startActive(motion)) {
							this.hitEffects.get(i)
									.add(new HitEffect(this.playerCharacters[i].getAttack(), upper, true, false));
						}
					}
				}
			}

			if (isHit[i]) {
				this.playerCharacters[i].setHitConfirm(true);
				this.playerCharacters[i].destroyAttackInstance();
			}

			if (!playerCharacters[i].isComboValid(currentFrame)) {
				playerCharacters[i].setHitCount(0);
			}
		}
	}

	/**
	 * Update attack parameters.
	 */
	protected void updateAttackParameter() {
		// Updates the parameters of all of projectiles appearing in the stage
		int dequeSize = this.projectileDeque.size();
		for (int i = 0; i < dequeSize; i++) {

			LoopEffect projectile = this.projectileDeque.removeFirst();
			if (projectile.getAttack().updateProjectileAttack()) {
				projectile.update();
				this.projectileDeque.addLast(projectile);
			}
		}

		// Updates the parameters of all of attacks excepted projectile
		// conducted by both characters
		for (int i = 0; i < 2; ++i) {
			if (this.playerCharacters[i].getAttack() != null) {
				if (!this.playerCharacters[i].getAttack().update(this.playerCharacters[i])) {
					this.playerCharacters[i].destroyAttackInstance();
				}
			}
		}
	}

	/**
	 * Update character states and effects.
	 */
	protected void updateCharacter() {
		for (int i = 0; i < 2; ++i) {
			// Updates each character.
			this.playerCharacters[i].update();

			if (this.playerCharacters[i].getAttack() != null) {
				if (this.playerCharacters[i].getAttack().isProjectile()) {

					Attack attack = this.playerCharacters[i].getAttack();
					ArrayList<Image> projectileImage = FlagSetting.enableWindow
							? GraphicManager.getInstance().getProjectileImageContainer() : null;

					if (this.playerCharacters[i].getAction() == Action.STAND_D_DF_FC) {
						projectileImage = FlagSetting.enableWindow
								? GraphicManager.getInstance().getUltimateAttackImageContainer() : null;
					}

					Image[] temp;
					if (FlagSetting.enableWindow) {
						temp = new Image[projectileImage.size()];
						for (int j = 0; j < temp.length; j++) {
							temp[j] = projectileImage.get(j);
						}
					} else {
						temp = null;
					}
					this.projectileDeque.addLast(new LoopEffect(attack, temp));
					this.playerCharacters[i].destroyAttackInstance();
				}
			}

			// Changes player's direction
			if (playerCharacters[i].isControl()) {
				playerCharacters[i].frontDecision(playerCharacters[i == 0 ? 1 : 0].getHitAreaCenterX());
			}

			// Updates the all of effects appearing in this stage
			for (int j = 0; j < this.hitEffects.get(i).size(); j++) {
				if (!this.hitEffects.get(i).get(j).update()) {
					this.hitEffects.get(i).remove(j);
					--j;
				}
			}
		}
		// Runs pushing.
		detectionPush();
		// Runs collision of first and second character.
		detectionFusion();
		// Runs effect when character's are in the end of stage.
		decisionEndStage();
	}

	/**
	 * Push the opponent characters based on the horizontal speed of P1 and P2.
	 */
	protected void detectionPush() {
		if (isCollision()) {
			int p1SpeedX = Math.abs(this.playerCharacters[0].getSpeedX());
			int p2SpeedX = Math.abs(this.playerCharacters[1].getSpeedX());

			if (p1SpeedX > p2SpeedX) {
				this.playerCharacters[1]
						.moveX(this.playerCharacters[0].getSpeedX() - this.playerCharacters[1].getSpeedX());
			} else if (p1SpeedX < p2SpeedX) {
				this.playerCharacters[0]
						.moveX(this.playerCharacters[1].getSpeedX() - this.playerCharacters[0].getSpeedX());
			} else {
				this.playerCharacters[0].moveX(this.playerCharacters[1].getSpeedX());
				this.playerCharacters[1].moveX(this.playerCharacters[0].getSpeedX());
			}
		}
	}

	/**
	 * If the positions of characters P1 and P2 overlap, update the coordinates of each character to prevent overlap.
	 */
	protected void detectionFusion() {
		if (isCollision()) {
			int direction = 0;

			// If first player is left
			if (this.playerCharacters[0].getHitAreaCenterX() < this.playerCharacters[1].getHitAreaCenterX()) {
				direction = 1;
				// If second player is left
			} else if (this.playerCharacters[0].getHitAreaCenterX() > this.playerCharacters[1].getHitAreaCenterX()) {
				direction = -1;
			} else {
				if (this.playerCharacters[0].isFront()) {
					direction = 1;
				} else {
					direction = -1;
				}
			}
			this.playerCharacters[0].moveX(-direction * 2);
			this.playerCharacters[1].moveX(direction * 2);
		}
	}

	/**
	 * Determine if characters P1 and P2 are in a collision state.
	 *
	 * @return {@code true} if both characters are colliding, {@code false} otherwise
	 */
	private boolean isCollision() {
		return this.playerCharacters[0].getHitAreaLeft() <= this.playerCharacters[1].getHitAreaRight()
				&& this.playerCharacters[0].getHitAreaTop() <= this.playerCharacters[1].getHitAreaBottom()
				&& this.playerCharacters[0].getHitAreaRight() >= this.playerCharacters[1].getHitAreaLeft()
				&& this.playerCharacters[0].getHitAreaBottom() >= this.playerCharacters[1].getHitAreaTop();
	}

	/**
	 * Update the coordinates of each character to prevent them from going off the stage's edge.
	 */
	protected void decisionEndStage() {
		for (int i = 0; i < 2; ++i) {
			// If action is down, character will be rebound.
			if (playerCharacters[i].getHitAreaRight() > GameSetting.STAGE_WIDTH) {
				if (playerCharacters[i].getAction() == Action.DOWN) {
					playerCharacters[i].reversalSpeedX();
				}
				playerCharacters[i].moveX(-playerCharacters[i].getHitAreaRight() + GameSetting.STAGE_WIDTH);

			} else if (playerCharacters[i].getHitAreaLeft() < 0) {
				if (playerCharacters[i].getAction() == Action.DOWN) {
					playerCharacters[i].reversalSpeedX();
				}
				playerCharacters[i].moveX(-playerCharacters[i].getHitAreaLeft());
			}
		}
	}

	/**
	 * Determine whether the next planned action is executable.
	 *
	 * @param character The instance of the character performing the action.
	 * @param nextAction The next planned action.
	 * @return {@code true} if the action is executable, {@code false} otherwise.
	 *
	 * @see Character
	 * @see Action
	 */
	protected boolean ableAction(Character character, Action nextAction) {
		Motion nextMotion = character.getMotionList().get(nextAction.ordinal());
		Motion nowMotion = character.getMotionList().get(character.getAction().ordinal());

		if (character.getEnergy() < -nextMotion.getAttackStartAddEnergy()) {
			return false;
		} else if (character.isControl()) {
			return true;
		} else {
			boolean checkFrame = nowMotion.getCancelAbleFrame() <= nowMotion.getFrameNumber()
					- character.getRemainingFrame();
			boolean checkAction = nowMotion.getCancelAbleMotionLevel() >= nextMotion.getMotionLevel();

			return character.isHitConfirm() && checkFrame && checkAction;
		}
	}

		/**
	 * Determine whether the attack has hit the opponent.
	 *
	 * @param opponent The opponent character.
	 * @param attack The attack that was launched.
	 * @return {@code true} if the attack hit the opponent, {@code false} otherwise.
	 *
	 * @see Character
	 * @see Attack
	 */
	protected boolean detectionHit(Character opponent, Attack attack) {
		if (attack == null || opponent.getState() == State.DOWN) {
			return false;
		} else if (opponent.getHitAreaLeft() <= attack.getCurrentHitArea().getRight()
				&& opponent.getHitAreaRight() >= attack.getCurrentHitArea().getLeft()
				&& opponent.getHitAreaTop() <= attack.getCurrentHitArea().getBottom()
				&& opponent.getHitAreaBottom() >= attack.getCurrentHitArea().getTop()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns an array containing information about characters P1 and P2.
	 *
	 * @return An array containing information about characters P1 and P2.
	 */

	public Character[] getCharacters() {
		return this.playerCharacters.clone();
	}

	/**
	 * Creates a frame data containing game information for the current frame.<br>
	 * It includes information about both characters, the current frame number, the current round,
	 * a list of Hadouken (energy wave) information for both characters, and key information for both characters.
	 *
	 * @param nowFrame The current frame.
	 * @param round The current round.
	 *
	 * @return A frame data containing game information for the current frame.
	 *
	 * @see KeyData
	 * @see FrameData
	 */
	public FrameData createFrameData(int nowFrame, int round) {
		CharacterData[] characterData = new CharacterData[] { new CharacterData(playerCharacters[0]),
				new CharacterData(playerCharacters[1]) };

		Deque<AttackData> newAttackDeque = new LinkedList<AttackData>();
		for (LoopEffect loopEffect : this.projectileDeque) {
			newAttackDeque.addLast(new AttackData(loopEffect.getAttack()));
		}

		return new FrameData(characterData, nowFrame, round, newAttackDeque);
	}

	/**
	 * Initializes character information and clears the contents of lists and queues at the start of a round.
	 */
	public void initRound() {
		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i].roundInit();
			this.hitEffects.get(i).clear();
		}

		this.projectileDeque.clear();
		this.inputCommands.clear();
	}

	/**
	 * Returns the list of effects for P1 and P2.
	 *
	 * @return The list of effects for P1 and P2.
	 */
	public LinkedList<LinkedList<HitEffect>> getHitEffectList() {
		return new LinkedList<LinkedList<HitEffect>>(this.hitEffects);
	}

	/**
	 * Returns the list of projectile data of both characters.
	 *
	 * @return the list of projectile data of both characters
	 */
	public Deque<LoopEffect> getProjectileDeque() {
		return new LinkedList<LoopEffect>(this.projectileDeque);
	}

	public void close(){
		for (Character character: this.getCharacters())
			character.close();
	}
}
