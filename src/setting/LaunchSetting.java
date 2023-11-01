package setting;

import enumerate.BackgroundType;
import grpc.GrpcServer;
import python.PyGatewayServer;

/**
 * A class that handles settings necessary for conducting matches, such as maximum character HP and match repetition count.
 */
public final class LaunchSetting {

	/**
	 * Arrays to store the maximum HP for P1 and P2.
	 */
	public static int[] maxHp = { 400, 400 };

	/**
	 * Arrays to store the maximum energy for P1 and P2.
	 */
	public static int[] maxEnergy = { 300, 300 };

	/**
	 * Arrays to store the AI names for P1 and P2.<br>
	 * "Keyboard" is stored for keyboard control.
	 */
	public static String[] aiNames = { "Keyboard", "Keyboard" };

	/**
	 * Character names for P1 and P2.
	 */
	public static String[] characterNames = { "ZEN", "ZEN" };
	
	public static String soundName = "Default";

	/**
	 * Device type in use.<br>
	 * {@code 0} if the device type is keyboard, or {@code 1} if AI.
	 */
	public static char[] deviceTypes = { 0, 0 };

	/**
	 * Port number to use for Python integration.
	 */
	public static int py4jPort = 4242;

	/**
	 * Number of repetitions for conducting matches.
	 */
	public static int repeatNumber = 1;

	/**
	 * Player number that will have pixel inversion.
	 */
	public static int invertedPlayer = 0;

	/**
	 * Background type.
	 */
	public static BackgroundType backgroundType = BackgroundType.IMAGE;

	/**
	 * Name of replay data.
	 */
	public static String replayName = "None";

	/**
	 * Counter for the number of match repetitions.
	 */
	public static int repeatedCount = 0;

	/**
	 * Gateway server for performing Java processing with Python.
	 */
	public static PyGatewayServer pyGatewayServer = null;

	/**
	 * Whether AI's visual data is disabled or not.
	 */
	public static boolean[] noVisual = {false, false};
	public static boolean[] nonDelay = {false, false};
	
	public static int grpcPort = 50051;
	
	public static GrpcServer grpcServer = null;
}
