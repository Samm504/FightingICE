package information container;

import java.util.ArrayList;

/**
 * A class that handles AI-related data stored in the AI ​​folder. <br>
 * Used when the round robin option (-a) is specified.
 */
public class AIContainer {

	/**
	 * A list that stores all AIs in the AI ​​folder.
	 */
	public static ArrayList<String> allAINameList = new ArrayList<String>();

	/**
	 * Index specifying AI of P1.
	 */
	public static int p1Index = 0;

	/**
	 * Index specifying AI of P2.
	 */
	public static int p2Index = 0;
}
