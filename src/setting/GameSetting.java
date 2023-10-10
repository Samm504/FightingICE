package setting;

/**
 * A class that handles the configuration of basic information about the game, such as screen width, FPS, and available characters.
 */
public class GameSetting {

    /**
     * The width of the game screen.
     */
    public static final int STAGE_WIDTH = 960;

    /**
     * The height of the game screen.
     */
    public static final int STAGE_HEIGHT = 640;
    
    public static final String TITLE_NAME = "DareFightingICE";

    /**
     * FPS setting for the game.
     */
    public static final int FPS = 60;

    /**
     * An array that stores the characters that can be used in the game.
     */
    public static final String[] CHARACTERS = {"ZEN", "GARNET", "LUD"};

    /**
     * An array that stores the default repeat numbers for the game.
     */
    public static final int[] REPEAT_NUMBERS = {1, 2, 3, 5, 10, 30, 50, 100};

    /**
     * Total number of frames in one round.
     */
//  public static final int ROUND_FRAME_NUMBER = 3600;
    public static int ROUND_FRAME_NUMBER = 3600;

    /**
     * Break time between rounds.
     */
    public static final int BREAKTIME_FRAME_NUMBER = 70;

    /**
     * Parameter to set the influence of gravity.
     */
    public static final int GRAVITY = 1;

    /**
     * Parameter to set the influence of friction.
     */
    public static final int FRICTION = 1;

    /**
     * Maximum number of key inputs to retain.
     */
    public static final int INPUT_LIMIT = 30;

    /**
     * Maximum number of rounds.
     */
    // public static final int ROUND_MAX = 3;
    public static int ROUND_MAX = 3;

    /**
     * Number of frames to determine combo continuation.<br>
     * If the number of frames from one attack hitting to the next attack hitting is less than or equal to this value, the combo continues.
     */
    public static final int COMBO_LIMIT = 30;

    /**
     * Additional frames (for slow motion).
     */
    public static final int ROUND_EXTRAFRAME_NUMBER = 180;

    /**
     * Audio sampling rate.
     */
    public static final int SOUND_SAMPLING_RATE = 48000;
    public static final int SOUND_RENDER_SIZE = GameSetting.SOUND_SAMPLING_RATE / GameSetting.FPS;
    public static final int SOUND_BUFFER_SIZE = (int)Math.pow(2, Math.ceil(Math.log(SOUND_RENDER_SIZE)/Math.log(2)));
}
