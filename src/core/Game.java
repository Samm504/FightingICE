package core;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import enumerate.BackgroundType;
import enumerate.GameSceneName;
import gamescene.Grpc;
import gamescene.HomeMenu;
import gamescene.Launcher;
import gamescene.Python;
import grpc.GrpcServer;
import image.LetterImage;
import informationcontainer.AIContainer;
import loader.ResourceLoader;
import manager.GameManager;
import manager.GraphicManager;
import manager.InputManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import setting.ResourceSetting;
import util.DeleteFiles;

/**
 * Class that sets the game's launch information and specifies the starting game scene.
 */
public class Game extends GameManager {

    /**
     * Class constructor that initializes the parent class GameManager.
     */
    public Game() {
        super();
    }

    /**
     * Sets the game's launch information based on the command-line arguments.
     *
     * @param options An array containing all the command-line arguments.
     */
    public void setOptions(String[] options) {
        // Reads the configurations here
        for (int i = 0; i < options.length; ++i) {
            switch (options[i]) {
                case "-a":
                case "--all":
                    FlagSetting.allCombinationFlag = true;
                    LaunchSetting.deviceTypes = new char[]{1, 1};
                    break;
                case "-n":
                case "--number":
                    LaunchSetting.repeatNumber = Integer.parseInt(options[++i]);
                    FlagSetting.automationFlag = true;
                    break;
                case "--a1":
                    LaunchSetting.aiNames[0] = options[++i];
                    LaunchSetting.deviceTypes[0] = InputManager.DEVICE_TYPE_AI;
                    break;
                case "--a2":
                    LaunchSetting.aiNames[1] = options[++i];
                    LaunchSetting.deviceTypes[1] = InputManager.DEVICE_TYPE_AI;
                    break;
                case "--c1":
                    LaunchSetting.characterNames[0] = getCharacterName(options[++i]);
                    break;
                case "--c2":
                    LaunchSetting.characterNames[1] = getCharacterName(options[++i]);
                    break;
                case "-da":
                    FlagSetting.debugActionFlag = true;
                    break;
                case "-df":
                    FlagSetting.debugFrameDataFlag = true;
                    break;
                case "-t":
                    FlagSetting.trainingModeFlag = true;
                    break;
                case "-del":
                    DeleteFiles.getInstance().deleteFiles();
                    break;
                case "-r":
                    // -r 100 -> 1 game has 100 rounds
                    GameSetting.ROUND_MAX = Integer.parseInt(options[++i]);
                    break;
                case "-f":
                    // -f 360 -> 1 round has 6 second
                    GameSetting.ROUND_FRAME_NUMBER = Integer.parseInt(options[++i]);
                    break;
                case "--black-bg":
                    LaunchSetting.backgroundType = BackgroundType.BLACK;
                    break;
                case "--grey-bg":
                    LaunchSetting.backgroundType = BackgroundType.GREY;
                    break;
                case "--inverted-player":
                    LaunchSetting.invertedPlayer = Integer.parseInt(options[++i]);
                    break;
                case "--mute":
                    FlagSetting.muteFlag = true;
                    break;
                case "--disable-window":
                    FlagSetting.enableWindow = false;
                //  FlagSetting.muteFlag = true;
                    FlagSetting.automationFlag = true;
                    break;
                case "--fastmode":
                    FlagSetting.fastModeFlag = true;
                    FlagSetting.automationFlag = true;
                    break;
                case "--json":
                    FlagSetting.jsonFlag = true;
                    break;
                case "--limithp":
                    // --limithp P1_HP P2_HP
                    FlagSetting.limitHpFlag = true;
                    LaunchSetting.maxHp[0] = Integer.parseInt(options[++i]);
                    LaunchSetting.maxHp[1] = Integer.parseInt(options[++i]);
                    break;
                case "--slow":
                    FlagSetting.slowmotion = true;
                    break;
                case "--err-log":
                    FlagSetting.outputErrorAndLogFlag = true;
                    break;
                case "--blind-player":
	                int blindPlayer = Integer.parseInt(options[++i]);
	                if (blindPlayer == 2) {
	                	LaunchSetting.noVisual[0] = LaunchSetting.noVisual[1] = true;
	                } else {
	                    LaunchSetting.noVisual[blindPlayer] = true;
	                }
                    break;
                case "--sound":
                    LaunchSetting.soundName = options[++i];
                	break;
                case "--non-delay":
                	int player = Integer.parseInt(options[++i]);
                	if (player == 2) {
                		LaunchSetting.nonDelay[0] = LaunchSetting.nonDelay[1] = true;
                	} else {
                		LaunchSetting.nonDelay[player] = true;
                	}
                	break;
                case "--py4j":
                    FlagSetting.py4j = true;
                    break;
                case "--port":
                	int port = Integer.parseInt(options[++i]);
                    LaunchSetting.py4jPort = LaunchSetting.grpcPort = port;
                    break;
                case "--grpc":
                	FlagSetting.grpc = true;
                	break;
                case "--grpc-auto":
                	FlagSetting.grpc = true;
                	FlagSetting.grpcAuto = true;
                	break;
                default:
                    Logger.getAnonymousLogger().log(Level.WARNING, "Arguments error: unknown format is exist. -> " + options[i] + " ?");
            }
        }

    }

    @Override
    public void initialize() {
        // Initialize the font to be used
        Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
        GraphicManager.getInstance().setLetterFont(new LetterImage(awtFont, true));

        createLogDirectories();

        if (FlagSetting.grpc) {
        	try {
        		LaunchSetting.grpcServer = new GrpcServer();
        		LaunchSetting.grpcServer.start(LaunchSetting.grpcPort);
			} catch (IOException e) {
                Logger.getAnonymousLogger().log(Level.INFO, "Fail to start gRPC server");
                FlagSetting.grpc = false;
			}
        }
        
        if ((FlagSetting.automationFlag || FlagSetting.allCombinationFlag) && !FlagSetting.py4j && !FlagSetting.grpc) {
            // If -n or -a is specified, launch the game directly without going to the menu screen
            if (FlagSetting.allCombinationFlag) {
                AIContainer.allAINameList = ResourceLoader.getInstance().loadFileNames("./data/ai", ".jar");

                if (AIContainer.allAINameList.size() < 2) {
                    Logger.getAnonymousLogger().log(Level.INFO, "Cannot launch FightingICE with Round-robin mode.");
                    this.isExitFlag = true;
                }
            }
            if (!LaunchSetting.soundName.equals("Default")) {
				ResourceSetting.SOUND_DIRECTORY = String.format("./data/sounds/%s/", LaunchSetting.soundName);
            }

            Launcher launcher = new Launcher(GameSceneName.PLAY);
            this.startGame(launcher);
        } else if (FlagSetting.py4j) {
        	// When starting from the Python side, start the game from the Python scene
            Python python = new Python();
            this.startGame(python);
        } else if (FlagSetting.grpcAuto) {
        	Grpc grpc = new Grpc();
        	this.startGame(grpc);
        } else {
            // If none of the above, start the game from the menu screen
            HomeMenu homeMenu = new HomeMenu();
            this.startGame(homeMenu);
        }

    }

    /**
     * Searches if the provided character name is valid and available among the playable characters.
     * If found, returns the character name; otherwise, issues a warning and returns "ZEN" as the default character.
     *
     * @param characterName The character name to search for.
     * @return The valid character name.
     */
    private String getCharacterName(String characterName) {
        for (String character : GameSetting.CHARACTERS) {
            if (character.equals(characterName)) {
                return character;
            }
        }
        Logger.getAnonymousLogger().log(Level.WARNING, characterName + " is does not exist. Please check the set character name.");
        return "ZEN"; // Default character
    }

    /**
     * Creates log directories if they do not exist.
     */
    private void createLogDirectories() {
        new File("log").mkdir();
        new File("log/replay").mkdir();
        new File("log/point").mkdir();
        new File("log/grpc").mkdir();
    }

    @Override
    public void close() {
        this.currentGameScene = null;
    }

}
