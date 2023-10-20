package simulator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import aiinterface.CommandCenter;
import command.CommandTable;
import enumerate.Action;
import fighting.Attack;
import fighting.Character;
import fighting.Fighting;
import fighting.LoopEffect;
import fighting.Motion;
import setting.GameSetting;
import struct.AttackData;
import struct.CharacterData;
import struct.FrameData;
import struct.Key;

/**
 * Class that handles battle processing and character information updates during simulation.
 */
public class SimFighting extends Fighting {

	/**
	 * Key inputs.
	 */
	private ArrayList<Deque<Key>> inputKeys;

	/**
	 * Input actions.
	 */
	private ArrayList<Deque<Action>> inputActions;

	/**
	 * Array to store instances of the CommandCenter class.
	 */
	private CommandCenter[] commandCenter;

	/**
	 * Class constructor.
	 */
	public SimFighting() {
		this.playerCharacters = new Character[2];
		this.projectileDeque = new LinkedList<LoopEffect>();
		this.commandTable = new CommandTable();

		this.inputKeys = new ArrayList<Deque<Key>>(2);
		this.inputActions = new ArrayList<Deque<Action>>(2);
		this.commandCenter = new CommandCenter[2];
	}

	/**
	 * Perform initialization.
	 *
	 * @param motionList
	 *            List containing motions for P1 and P2.
	 * @param actionList
	 *            List containing actions for P1 and P2.
	 * @param frameData
	 *            Frame data at the start of simulation.
	 * @param playerNumber
	 *            Boolean value identifying P1/P2. {@code true} if the player is P1, or {@code false} if P2.
	 */
	public void initialize(ArrayList<ArrayList<Motion>> motionList, ArrayList<Deque<Action>> actionList,
			FrameData frameData, boolean playerNumber) {

		for (int i = 0; i < 2; i++) {
			this.playerCharacters[i] = new Character(frameData.getCharacter(i == 0), motionList.get(i));

			this.inputKeys.add(this.playerCharacters[i].getProcessedCommand());
			this.inputActions.add(actionList.get(i));

			this.commandCenter[i] = new CommandCenter();
			this.commandCenter[i].setFrameData(frameData, i == 0);
		}

		Deque<AttackData> projectiles = frameData.getProjectiles();
		for (AttackData temp : projectiles) {
			this.projectileDeque.addLast(new LoopEffect(new Attack(temp), null));
		}
	}

	/**
	 * Perform battle processing for one frame. <br>
	 * The processing order is as follows: <br>
	 * <ol>
	 * <li>Execute actions based on key inputs.</li>
	 * <li>Handle collision detection of attacks and update parameters such as character HP.</li>
	 * <li>Update attack parameters.</li>
	 * <li>Update character states.</li>
	 * </ol>
	 *
	 * @param currentFrame
	 *            Current frame.
	 */
	public void processingFight(int currentFrame) {
		// 1. Execute commands and battle processing
		processingCommands();
		// 2. Handle collision detection
		calculationHit(currentFrame);
		// 3. Update attack parameters
		updateAttackParameter();
		// 4. Update character information
		updateCharacter();
	}

	/**
	 * Execute actions based on the key inputs and actions provided at the start of the simulation.
	 */
	public void processingCommands() {

		for (int i = 0; i < 2; i++) {
			Deque<Key> keyList = this.inputKeys.get(i);
			Deque<Action> actList = this.inputActions.get(i);

			if (keyList.size() > GameSetting.INPUT_LIMIT - 1) {
				keyList.removeFirst();
			}

			if (!this.playerCharacters[i].getInputCommand().isEmpty()) {
				Deque<Key> temp = this.playerCharacters[i].getProcessedCommand();
				keyList.addLast(temp.removeFirst());
				this.playerCharacters[i].setInputCommand(temp);
				keyList.add(new Key(this.playerCharacters[i].getInputCommand().getFirst()));

				Action act = this.commandTable.interpretationCommandFromKey(this.playerCharacters[i], keyList);
				if (ableAction(this.playerCharacters[i], act)) {
					this.playerCharacters[i].runAction(act, true);
				}

			} else if (actList != null) {
				if (!actList.isEmpty()) {

					if (ableAction(this.playerCharacters[i], actList.getFirst()) && !commandCenter[i].getSkillFlag()) {
						this.commandCenter[i].commandCall(actList.removeFirst().name());

					} else if (this.playerCharacters[i].isControl() && !this.commandCenter[i].getSkillFlag()) {
						actList.removeFirst();
					}
				}

				this.inputKeys.get(i).add(this.commandCenter[i].getSkillKey());
				Action act = this.commandTable.interpretationCommandFromKey(this.playerCharacters[i], keyList);
				this.playerCharacters[i].setInputCommand(this.commandCenter[i].getSkillKeys());

				if (ableAction(this.playerCharacters[i], act)) {
					this.playerCharacters[i].runAction(act, true);
				}
			}
		}
	}

	@Override
	protected void calculationHit(int currentFrame) {
		boolean[] isHit = { false, false };

		// Handle projectile attacks
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

		// Handle normal attacks
		for (int i = 0; i < 2; i++) {
			int opponentIndex = i == 0 ? 1 : 0;
			Attack attack = this.playerCharacters[i].getAttack();

			if (detectionHit(this.playerCharacters[opponentIndex], attack)) {
				isHit[i] = true;
				// Update parameters such as HP
				// The 'character' object is shared with 'Fighting', so sound effects are executed
				this.playerCharacters[opponentIndex].hitAttack(this.playerCharacters[i], attack, currentFrame);
			}
		}

		for (int i = 0; i < 2; i++) {
			if (isHit[i]) {
				this.playerCharacters[i].setHitConfirm(true);
				this.playerCharacters[i].destroyAttackInstance();
			}

			if (!this.playerCharacters[i].isComboValid(currentFrame)) {
				this.playerCharacters[i].setHitCount(0);
			}
		}
	}

	@Override
	protected void updateAttackParameter() {
		 // Updates the parameters of all projectiles appearing in the stage
		int dequeSize = this.projectileDeque.size();
		for (int i = 0; i < dequeSize; i++) {

			LoopEffect projectile = this.projectileDeque.removeFirst();
			if (projectile.getAttack().updateProjectileAttack()) {
				this.projectileDeque.addLast(projectile);
			}
		}

		// Updates the parameters of all attacks except projectiles
		// conducted by both characters
		for (int i = 0; i < 2; ++i) {
			if (this.playerCharacters[i].getAttack() != null) {
				if (!this.playerCharacters[i].getAttack().update(this.playerCharacters[i])) {
					this.playerCharacters[i].destroyAttackInstance();
				}
			}
		}
	}

	@Override
	protected void updateCharacter() {
		for (int i = 0; i < 2; ++i) {
			// Update each character
			this.playerCharacters[i].update();

			// Enqueue object attack if the data is a missile decision
			if (this.playerCharacters[i].getAttack() != null) {
				if (this.playerCharacters[i].getAttack().isProjectile()) {

					this.projectileDeque.addLast(new LoopEffect(this.playerCharacters[i].getAttack(), null));
					this.playerCharacters[i].destroyAttackInstance();
				}
			}

			// Change player's direction
			if (playerCharacters[i].isControl()) {
				playerCharacters[i].frontDecision(playerCharacters[i == 0 ? 1 : 0].getHitAreaCenterX());
			}
		}
		// Run pushing effect
		detectionPush();
		// Run collision of the first and second character.
		detectionFusion();
		// Run effect when characters are at the end of the stage.
		decisionEndStage();
	}

	@Override
	public FrameData createFrameData(int nowFrame, int round) {
		CharacterData[] characterData = new CharacterData[2];
		for (int i = 0; i < 2; i++) {
			characterData[i] = new CharacterData(this.playerCharacters[i]);
			characterData[i].setProcessedCommand(this.inputKeys.get(i));
		}

		Deque<AttackData> newAttackDeque = new LinkedList<AttackData>();
		for (LoopEffect loopEffect : this.projectileDeque) {
			newAttackDeque.addLast(new AttackData(loopEffect.getAttack()));
		}

		return new FrameData(characterData, nowFrame, round, newAttackDeque);
	}
}
