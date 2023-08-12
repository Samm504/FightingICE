package aiinterface;

import java.util.LinkedList;

import grpc.PlayerAgent;
import informationcontainer.RoundResult;
import manager.InputManager;
import setting.LaunchSetting;
import struct.AudioData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.ScreenData;

/**
 * Class that manages AI threads and processes.
 */
public class AIController extends Thread {

	private char deviceType;
    /**
     * Interface defining methods to be implemented by the AI.
     */
    private AIInterface ai;
    private PlayerAgent grpc;
    
    //private GameData gameData;

    /**
     * The character's side flag.<br>
     * {@code true} if the character is P1, or {@code false} if P2.
     */
    private boolean playerNumber;

    /**
     * Flag indicating whether a match has started.
     */
    private boolean isFighting;

    /**
     * Entered keys.
     */
    private Key key;

    /**
     * Frame delay.
     */
    private final static int DELAY = 15;

    /**
     * List to store frame data.
     */
    private LinkedList<FrameData> framesData;

    /**
     * Data containing screen information.
     */
    private ScreenData screenData;

    private AudioData audioData;
    
    private boolean isRoundEnd;
    private RoundResult roundResult;

    /**
     * Synchronization object for starting each AI process simultaneously.
     */
    private Object waitObj;
    
    //private List<Double> durations = new ArrayList<>();

    /**
     * Class constructor that sets the provided AI interface and initializes AIController.
     *
     * @param ai AI interface defining methods to be implemented by the AI
     * @see AIInterface
     */
    public AIController(AIInterface ai) {
        this.ai = ai;
        this.deviceType = InputManager.DEVICE_TYPE_AI;
    }
    
    public AIController(PlayerAgent grpc) {
    	this.grpc = grpc;
    	this.deviceType = InputManager.DEVICE_TYPE_GRPC;
    }
    /**
     * Initializes with the provided parameters.
     *
     * @param waitFrame    Synchronization object for starting each AI process
     * @param gameData     Instance of a class containing immutable game information such as screen width and max HP
     * @param playerNumber the character's side flag: {@code true} if P1, {@code false} if P2
     * @see GameData
     */
    public void initialize(Object waitFrame, GameData gameData, boolean playerNumber) {
        this.waitObj = waitFrame;
        //this.gameData = gameData;
        this.playerNumber = playerNumber;
        this.key = new Key();
        this.framesData = new LinkedList<FrameData>();
        this.clear();
        this.isFighting = true;
        this.isRoundEnd = false;
//		boolean isInit = false;
//		while(!isInit)
        
//		try {
        if (this.deviceType == InputManager.DEVICE_TYPE_AI) {
        	this.ai.initialize(gameData, playerNumber);
        } else if (this.deviceType == InputManager.DEVICE_TYPE_GRPC) {
        	this.grpc.initialize(gameData, playerNumber);
        }
//			isInit = true;
//		} catch (Py4JException e) {
//			Logger.getAnonymousLogger().log(Level.SEVERE, "Cannot Initialize AI");
//			InputManager.getInstance().createAIcontroller();
//		}
    }
    
    public Key input() {
    	return this.key;
    }
    
    @Override
    public void run() {
        while (isFighting) {
            synchronized (this.waitObj) {
                try {
                    this.waitObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (isRoundEnd) {
            	this.grpc.onRoundEnd(roundResult);
            	this.isRoundEnd = false;
            	this.roundResult = null;
            } else {
            	boolean isControl;

                try {
                    isControl = this.framesData.getLast().getCharacter(this.playerNumber).isControl();
                } catch (NullPointerException e) {
                    // while game is not started
                    isControl = false;
                }

                FrameData frameData = !this.framesData.isEmpty() ? new FrameData(this.framesData.removeFirst()) : new FrameData();
                
                if (this.deviceType == InputManager.DEVICE_TYPE_AI) {
                	this.ai.getInformation(frameData, isControl);
        	        this.ai.getAudioData(this.audioData);
        	        // screen raw data isn't provided to sound-only AI
        	        if (!LaunchSetting.noVisual[this.playerNumber ? 0: 1]){
        	            this.ai.getScreenData(this.screenData);
        	        } else {
        	        	frameData.removeVisualData();
        	        }
        	        
        	        this.ai.processing();
        	        this.setInput(this.ai.input());
                } else if (this.deviceType == InputManager.DEVICE_TYPE_GRPC) {
                	if (this.grpc.isReady()) {
                		this.grpc.setInformation(isControl, frameData, audioData, screenData, this.framesData.getLast());
                    	this.grpc.onGameUpdate();
                	}
                }
            }
	        ThreadController.getInstance().notifyEndProcess(this.playerNumber);
        }
    }

    /**
     * Returns input information from the AI.<br>
     * Returns an empty key if there is no input information.
     *
     * @return Input information from the AI
     * @see Key
     */
    public synchronized Key getInput() {
        if (this.key != null) {
            return this.key;
        } else {
            return new Key();
        }
    }

    /**
     * Sets input information from the AI.
     *
     * @param key Input information from the AI
     */
    public synchronized void setInput(Key key) {
        this.key = new Key(key);
    }

    /**
     * Sets frame data after a match process.<br>
     * If the list size is greater than DELAY, removes the oldest frame data.
     *
     * @param fd Frame data after a match process
     * @see FrameData
     */
    public synchronized void setFrameData(FrameData fd) {
        if (fd != null) {
            this.framesData.addLast(fd);
        } else {
            this.framesData.addLast(new FrameData());
        }

        while (this.framesData.size() > DELAY) {
            this.framesData.removeFirst();
        }
    }

    /**
     * Sets screen data after a match process.<br>
     *
     * @param screenData Screen data after a match process
     * @see ScreenData
     */
    public synchronized void setScreenData(ScreenData screenData) {
        this.screenData = screenData;
    }

    public synchronized void setAudioData(AudioData audioData) {
        this.audioData = audioData;
    }

    /**
     * Clears the stored frame data in the list.<br>
     * Then adds DELAY-1 empty frame data to the list.
     */
    public synchronized void clear() {
        if (this.framesData != null) {
            this.framesData.clear();

            while (this.framesData.size() < DELAY) {
                this.framesData.add(new FrameData());
            }
        }
    }
    
    /**
     * Informs the AI about the current round result.
     *
     * @param roundResult The current round result at the end of a round
     * @see RoundResult
     */
    public synchronized void informRoundResult(RoundResult roundResult) {
        if (this.deviceType == InputManager.DEVICE_TYPE_AI) {
        	this.ai.roundEnd(roundResult.getRemainingHPs()[0], roundResult.getRemainingHPs()[1], roundResult.getElapsedFrame());
        } else if (this.deviceType == InputManager.DEVICE_TYPE_GRPC) {
        	this.isRoundEnd = true;
        	this.roundResult = roundResult;
        }
    }

    /**
     * Notifies the end of the match and performs AI's cleanup.
     */
    public synchronized void gameEnd() {
        this.isFighting = false;
        if (this.deviceType == InputManager.DEVICE_TYPE_AI) {
            this.ai.close();
    	}
        synchronized (this.waitObj) {
            this.waitObj.notifyAll();
        }
    }
}
