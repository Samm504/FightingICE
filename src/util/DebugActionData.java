package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import fighting.Character;
import loader.ResourceLoader;
import setting.LaunchSetting;

public class DebugActionData {

	/**
	 * A list containing maps that store action names and their total frame counts
	 * for counting the actions of P1 and P2.
	 * Index 0: P1; Index 1: P2
	 */
	private ArrayList<HashMap<String, Integer>> actionList;

	/**
	 * A list containing maps that store action names and their counts within one round
	 * for counting the actions of P1 and P2.
	 * Index 0: P1; Index 1: P2
	 */
	private ArrayList<HashMap<String, Integer>> countedActionContainer;

	/**
	 * BufferedReader for file reading.
	 * Index 0: P1; Index 1: P2
	 */
	private BufferedReader[] bReaders;

	/**
	 * PrintWriter for file writing.
	 * Index 0: P1; Index 1: P2
	 */
	private PrintWriter[] pWriters;

	/**
	 * An array containing all action names.
	 */
	private final String[] motionName = { "FORWARD_WALK", "DASH", "BACK_STEP", "JUMP", "FOR_JUMP", "BACK_JUMP",
			"STAND_GUARD", "CROUCH_GUARD", "AIR_GUARD", "THROW_A", "THROW_B", "STAND_A", "STAND_B", "CROUCH_A",
			"CROUCH_B", "AIR_A", "AIR_B", "AIR_DA", "AIR_DB", "STAND_FA", "STAND_FB", "CROUCH_FA", "CROUCH_FB",
			"AIR_FA", "AIR_FB", "AIR_UA", "AIR_UB", "STAND_D_DF_FA", "STAND_D_DF_FB", "STAND_F_D_DFA", "STAND_F_D_DFB",
			"STAND_D_DB_BA", "STAND_D_DB_BB", "AIR_D_DF_FA", "AIR_D_DF_FB", "AIR_F_D_DFA", "AIR_F_D_DFB", "AIR_D_DB_BA",
			"AIR_D_DB_BB", "STAND_D_DF_FC" };

	/**
	 * Get the sole instance of the DebugActionData class.
	 *
	 * @return The sole instance of the DebugActionData class.
	 */
	public static DebugActionData getInstance() {
		return DebugActionDataHolder.instance;
	}

	/**
	 * Holder class that creates an instance only when getInstance() is called.
	 */
	private static class DebugActionDataHolder {
		private static final DebugActionData instance = new DebugActionData();
	}

	/**
	 * Class constructor.
	 */
	private DebugActionData() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + DebugActionData.class.getName());
		Logger.getAnonymousLogger().log(Level.INFO, "Start debug action mode...");
	}

	/**
	 * Initialize the DebugActionData class.
	 * 1. Initialize field variables.
	 * 2. Open output files, and write action names as header information.
	 * 3. Store action names and their total frame counts in a list.
	 */
	public void initialize() {
		this.actionList = new ArrayList<HashMap<String, Integer>>(2);
		this.countedActionContainer = new ArrayList<HashMap<String, Integer>>(2);
		this.pWriters = new PrintWriter[2];
		this.bReaders = new BufferedReader[2];

		for (int i = 0; i < 2; i++) {
			this.actionList.add(new HashMap<String, Integer>());
			this.countedActionContainer.add(new HashMap<String, Integer>());
		}

		String path = "./debugActionData";
		new File(path).mkdir();

		// Open reading and writing files
		for (int i = 0; i < 2; i++) {
			String fileName = "/" + (i == 0 ? "P1" : "P2") + "ActionFile.csv";
			this.pWriters[i] = ResourceLoader.getInstance().openWriteFile(path + fileName, true);
			this.bReaders[i] = ResourceLoader.getInstance().openReadFile(path + fileName);

			writeHeader(i);
			readMotionData(i);
		}
	}

	/**
	 * Count the number of actions performed by P1 and P2.
	 * Counts actions only when they are executed.
	 * Actions that are not counted or actions in progress are not counted.
	 *
	 * @param characters An array containing character data for P1 and P2.
	 *                   Index 0: P1; Index 1: P2
	 */
	public void countPlayerAction(Character[] characters) {
		String[] actionNames = new String[] { characters[0].getAction().name(), characters[1].getAction().name() };
		int[] remainingFrames = new int[] { characters[0].getRemainingFrame(), characters[1].getRemainingFrame() };

		for (int i = 0; i < 2; i++) {
			if (canCount(this.countedActionContainer.get(i), actionNames[i], remainingFrames[i])) {
				this.countedActionContainer.get(i).replace(actionNames[i],
						this.countedActionContainer.get(i).get(actionNames[i]) + 1);
			}
		}
	}

	/**
	 * Output the count of each action for P1 and P2 to a CSV file.
	 */
	public void outputActionCount() {
		for (int i = 0; i < 2; i++) {
			for (String string : this.motionName) {
				this.pWriters[i].print(this.countedActionContainer.get(i).get(string) + ",");
				this.countedActionContainer.get(i).replace(string, 0);
			}

			this.pWriters[i].println();
			this.pWriters[i].flush();
		}
	}

	/**
	 * Close all output files and clear the contents of the lists.
	 */
	public void closeAllWriters() {
		for (int i = 0; i < 2; i++) {
			this.pWriters[i].close();
		}
		this.actionList.clear();
		this.countedActionContainer.clear();
	}

	/**
	 * Write action names as header information to the output file.
	 *
	 * @param i Player index: 0 for P1, 1 for P2.
	 */
	private void writeHeader(int i) {
		try {
			if (this.bReaders[i].read() == -1) {
				for (String string : this.motionName) {
					this.pWriters[i].print(string + ",");
				}
				this.pWriters[i].println();
			} else {
				this.pWriters[i].println();
				this.pWriters[i].println();
			}

			this.bReaders[i].close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read action names and their total frame counts and store them in a list.
	 *
	 * @param i Player index: 0 for P1, 1 for P2.
	 */
	private void readMotionData(int i) {
		String fileName = "./data/characters/" + LaunchSetting.characterNames[i] + "/Motion.csv";
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s;
			while ((s = br.readLine()) != null) {
				String array[] = s.split(","); // Split by comma

				for (String string : this.motionName) {
					if (string.equals(array[0])) {
						this.actionList.get(i).put(string, Integer.parseInt(array[1]));
						this.countedActionContainer.get(i).put(string, 0);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determine whether an action should be counted.
	 *
	 * @param temp           The map containing action names and their total frame counts.
	 * @param actionName     The name of the action currently performed by the character.
	 * @param remainingFrame The remaining frame count of the currently performed action.
	 *
	 * @return true if the action should be counted, false otherwise.
	 */
	private boolean canCount(HashMap<String, Integer> temp, String actionName, int remainingFrame) {
		if (temp.containsKey(actionName)) {
			return temp.get(actionName) == remainingFrame - 1;
		} else {
			return false;
		}
	}
}
