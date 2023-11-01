package setting;

/**
 * A class that manages flags related to the functionality of the game.
 */
public class FlagSetting {

    /**
     * Flag to determine whether to conduct repeated battles.
     */
    public static boolean automationFlag = false;

    /**
     * Flag to determine whether to conduct battles for all AI combinations.
     */
    public static boolean allCombinationFlag = false;

    /**
     * Flag to determine whether to use background images.
     */
    public static boolean enableBackground = true;

    /**
     * Flag to determine whether to use Python integration.
     */
    public static boolean py4j = false;

    /**
     * Flag to determine whether to log the number of actions performed.
     */
    public static boolean debugActionFlag = false;

    /**
     * Not used.
     */
    public static boolean debugFrameDataFlag = false;

    /**
     * Flag to determine whether to use training mode.
     */
    public static boolean trainingModeFlag = false;

    /**
     * Flag to determine whether to set maximum HP for P1 and P2.
     */
    public static boolean limitHpFlag = false;

    /**
     * Flag to determine whether to mute the audio.
     */
    public static boolean muteFlag = false;

    /**
     * Flag to determine whether to output match data to a JSON file.
     */
    public static boolean jsonFlag = false;

    /**
     * Flag to determine whether to output error logs.
     */
    public static boolean outputErrorAndLogFlag = false;

    /**
     * Flag to determine whether to launch in Fast Mode.
     */
    public static boolean fastModeFlag = false;

    /**
     * Flag to determine whether to create a game window.
     */
    public static boolean enableWindow = true;

    /**
     * Flag to determine whether to trigger slow motion at the end of a round.
     */
    public static boolean slowmotion = false;
    
    public static boolean grpc = false;
    public static boolean grpcAuto = false;
}
