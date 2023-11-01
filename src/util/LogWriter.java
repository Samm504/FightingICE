package util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

import fighting.Character;
import informationcontainer.RoundResult;
import input.KeyData;
import loader.ResourceLoader;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.AttackData;
import struct.CharacterData;
import struct.FrameData;
import struct.HitArea;
import struct.Key;

public class LogWriter {

    /**
     * Constant to specify the file extension for outputting match results as .csv files.
     */
    public static final int CSV = 0;

    /**
     * Constant to specify the file extension for outputting match results as .txt files.
     */
    public static final int TXT = 1;

    /**
     * Constant to specify the file extension for outputting match results as .PLOG files.
     */
    public static final int PLOG = 2;

    /**
     * This variable stores the current round.<br>
     * It is updated every time updateJson() is called.<br>
     * It is used to realize when the round changes.
     */
    private int currentRound = 1;

    /** Stream generator for JSON. */
    private JsonGenerator generator;

    /**
     * A flag marking whether to include display information in instances of FrameData.
     */
    boolean disableDisplayDataInFrameData;

    /**
     * Class constructor.
     */
    private LogWriter() {
        Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + LogWriter.class.getName());
    }

    /**
     * Holder class that generates an instance when getInstance() is called for the first time.
     */
    private static class LogWriterHolder {
        private static final LogWriter instance = new LogWriter();
    }

    /**
     * Get the singleton instance of the LogWriter class.
     *
     * @return The singleton instance of the LogWriter class.
     */
    public static LogWriter getInstance() {
        return LogWriterHolder.instance;
    }

    /**
     * Output match results to a file with the specified extension.<br>
     * The current time information is used in the output file name.
     *
     * @param roundResults List containing the results of each round.
     * @param extension    The specified file extension.
     * @param timeInfo     The current time information.
     */
    public void outputResult(ArrayList<RoundResult> roundResults, int extension, String timeInfo) {
        String path = "./log/point/";
        String fileName = createOutputFileName(path, timeInfo);

        PrintWriter pw;
        switch (extension) {
            case CSV:
                pw = ResourceLoader.getInstance().openWriteFile(fileName + ".csv", false);
                break;
            case TXT:
                pw = ResourceLoader.getInstance().openWriteFile(fileName + ".txt", false);
                break;
            default:
                pw = ResourceLoader.getInstance().openWriteFile(fileName + ".PLOG", false);
                break;
        }

        for (RoundResult roundResult : roundResults) {
            int[] score = roundResult.getRemainingHPs();

            pw.println(roundResult.getRound() + "," + score[0] + "," + score[1] + "," + roundResult.getElapsedFrame());
        }

        pw.close();
    }

    /**
     * Output the log for a replay file.<br>
     * Write the character information and key input data for the current frame.
     *
     * @param dos              DataOutputStream for writing to the replay file.
     * @param keyData          KeyData instance.
     * @param playerCharacters Array containing the characters of P1 and P2.
     */
    public void outputLog(DataOutputStream dos, KeyData keyData, Character[] playerCharacters) {
        // Output log file for replay
        try {
            for (int i = 0; i < 2; ++i) {
                dos.writeBoolean(playerCharacters[i].isFront());
                dos.writeByte((byte) playerCharacters[i].getRemainingFrame());
                dos.writeByte((byte) playerCharacters[i].getAction().ordinal());
                dos.writeInt(playerCharacters[i].getHp());
                dos.writeInt(playerCharacters[i].getEnergy());
                dos.writeInt(playerCharacters[i].getX());
                dos.writeInt(playerCharacters[i].getY());

                byte input = (byte) (convertBtoI(keyData.getKeys()[i].A) +
                                    convertBtoI(keyData.getKeys()[i].B) * 2 +
                                    convertBtoI(keyData.getKeys()[i].C) * 4 +
                                    convertBtoI(keyData.getKeys()[i].D) * 8 +
                                    convertBtoI(keyData.getKeys()[i].L) * 16 +
                                    convertBtoI(keyData.getKeys()[i].R) * 32 +
                                    convertBtoI(keyData.getKeys()[i].U) * 64);

                dos.writeByte(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write header information to the replay file, including game mode (HP mode or Time mode) and character selection.
     *
     * @param dos DataOutputStream for writing to the replay file.
     */
    public void writeHeader(DataOutputStream dos) {
        try {
            for (int i = 0; i < 2; i++) {
                if (FlagSetting.limitHpFlag) {
                    dos.writeInt(-1);
                    dos.writeInt(LaunchSetting.maxHp[i]);
                }

                dos.writeInt(Arrays.asList(GameSetting.CHARACTERS).indexOf(LaunchSetting.characterNames[i]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate the output file name.<br>
     * The format is "path + game mode + P1 AI name + P2 AI name + current time".
     *
     * @param path     The path where the file will be output.
     * @param timeInfo The current time information.
     * @return The output file name.
     */
    public String createOutputFileName(String path, String timeInfo) {
        String mode = FlagSetting.limitHpFlag ? "HPMode" : "TimeMode";

        return path + mode + "_" + LaunchSetting.aiNames[0] + "_" + LaunchSetting.aiNames[1] + "_" + timeInfo;
    }

    /**
     * Convert a boolean variable to an integer.
     *
     * @param b The boolean variable to be converted.
     * @return 1 if the argument is true, 0 if it is false.
     */
    private int convertBtoI(boolean b) {
        return b ? 1 : 0;
    }

    /**
     * Initialize a JSON generator and write initial information about the match.<br>
     * The JSON structure includes data such as max HP, character names, stage size, and more.
     *
     * @param jsonName The file name for the JSON file.
     */
    public void initJson(String jsonName) {
        File file = new File(jsonName);

        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            this.generator = Json.createGenerator(fos);

            // Open root object
            this.generator.writeStartObject();

            // Write max HP
            this.generator.writeStartObject("max_hp");
            this.generator.write("P1", LaunchSetting.maxHp[0]);
            this.generator.write("P2", LaunchSetting.maxHp[1]);
            this.generator.writeEnd();

            // Write character names
            this.generator.writeStartObject("character_names");
            this.generator.write("P1", LaunchSetting.characterNames[0]);
            this.generator.write("P2", LaunchSetting.characterNames[1]);
            this.generator.writeEnd();

            // Write stage details
            this.generator.writeStartObject("stage_size");
            this.generator.write("x", GameSetting.STAGE_WIDTH);
            this.generator.write("y", GameSetting.STAGE_HEIGHT);
            this.generator.writeEnd();

            // TODO: Combo tables

            // Open rounds array
            this.generator.writeStartArray("rounds");

            // Open frames array
            this.generator.writeStartArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write data for a frame in JSON format using the JSON generator.
     *
     * @param frameData    The frame data.
     * @param keyDataInput Data about keys input for this frame.
     */
    public void updateJson(FrameData frameData, KeyData keyDataInput) {
        // Check if this is a new round
        if (frameData.getRound() != this.currentRound) {
            this.generator.writeEnd();
            this.generator.writeStartArray();
            this.currentRound = frameData.getRound();
        }

        // Open frame object
        this.generator.writeStartObject();

        this.generator.write("current_frame", frameData.getFramesNumber());
        this.generator.write("remaining_frames", frameData.getRemainingFramesNumber());

        // Write P1 data
        this.generator.writeStartObject("P1");
        this.writeCharacterDataToJson(frameData.getCharacter(true), keyDataInput.getKeys()[0], frameData.getProjectilesByP1());
        this.generator.writeEnd();

        // Write P2 data
        this.generator.writeStartObject("P2");
        this.writeCharacterDataToJson(frameData.getCharacter(false), keyDataInput.getKeys()[1], frameData.getProjectilesByP2());
        this.generator.writeEnd();

        // Close frame object
        this.generator.writeEnd(); // Players data

        this.generator.flush();
    }

    /**
     * Write character data to JSON using the JSON generator.
     *
     * @param cd          Character data.
     * @param keys        Keys input by the character.
     * @param projectiles Projectiles generated by this player.
     */
    private void writeCharacterDataToJson(CharacterData cd, Key keys, Deque<AttackData> projectiles) {

        // Character
        this.generator.write("front", cd.isFront());
        this.generator.write("remaining_frames", cd.getRemainingFrame());
        this.generator.write("action", cd.getAction().toString());
        this.generator.write("action_id", cd.getAction().ordinal());
        this.generator.write("state", cd.getState().toString());
        this.generator.write("state_id", cd.getState().ordinal());
        this.generator.write("hp", cd.getHp());
        this.generator.write("energy", cd.getEnergy());
        this.generator.write("x", cd.getX());
        this.generator.write("y", cd.getY());
        this.generator.write("left", cd.getLeft());
        this.generator.write("right", cd.getRight());
        this.generator.write("top", cd.getTop());
        this.generator.write("bottom", cd.getBottom());
        this.generator.write("speed_x", cd.getSpeedX());
        this.generator.write("speed_y", cd.getSpeedY());

        // Agent decision
        this.generator.write("key_a", keys.A);
        this.generator.write("key_b", keys.B);
        this.generator.write("key_c", keys.C);
        this.generator.write("key_up", keys.U);
        this.generator.write("key_down", keys.D);
        this.generator.write("key_left", keys.L);
        this.generator.write("key_right", keys.R);

        // Attack
        AttackData attack = cd.getAttack();
        if (attack != null && attack.getAttackType() != 0) {
            this.generator.writeStartObject("attack"); // Attack
            this.writeAttackToJson(attack);
            this.generator.writeEnd(); // Attack
        }

        this.generator.writeStartArray("projectiles"); // Projectiles
        for (AttackData projectile : projectiles) {
            this.generator.writeStartObject(); // Projectile
            this.writeAttackToJson(projectile);
            this.generator.writeEnd(); // Projectile
        }
        this.generator.writeEnd(); // Projectiles
    }

    /**
     * Write attack data to JSON using the JSON generator.
     *
     * @param attack Data about the attack.
     */
    private void writeAttackToJson(AttackData attack) {
        this.generator.write("speed_x", attack.getSpeedX());
        this.generator.write("speed_y", attack.getSpeedY());
        this.generator.write("hit_damage", attack.getHitDamage());
        this.generator.write("guard_damage", attack.getGuardDamage());
        this.generator.write("start_add_energy", attack.getStartAddEnergy());
        this.generator.write("hit_add_energy", attack.getHitAddEnergy());
        this.generator.write("guard_add_energy", attack.getGuardAddEnergy());
        this.generator.write("give_energy", attack.getGiveEnergy());
        this.generator.write("give_guard_recov", attack.getGiveGuardRecov());
        int attackType = attack.getAttackType();
        switch (attackType) {
            case 1:
                this.generator.write("attack_type", "HIGH");
                break;
            case 2:
                this.generator.write("attack_type", "MIDDLE");
                break;
            case 3:
                this.generator.write("attack_type", "LOW");
                break;
            case 4:
                this.generator.write("attack_type", "THROW");
                break;
            default:
                throw new IllegalArgumentException("Unexpected attack type: " + attackType);
        }
        this.generator.write("attack_type_id", attackType);
        this.generator.write("impact_x", attack.getImpactX());
        this.generator.write("impact_y", attack.getImpactY());

        HitArea hitArea = attack.getCurrentHitArea();
        this.generator.writeStartObject("hit_area"); // Hit area
        this.generator.write("bottom", hitArea.getBottom());
        this.generator.write("top", hitArea.getTop());
        this.generator.write("left", hitArea.getLeft());
        this.generator.write("right", hitArea.getRight());
        this.generator.writeEnd(); // Hit area
    }

    /**
     * Close any remaining open JSON tags and then close the generator.
     */
    public void finalizeJson() {
        // Close rounds array
        this.generator.writeEnd();

        // Close frames array
        this.generator.writeEnd();

        // Close root object
        this.generator.writeEnd();

        // Close the resources
        this.generator.flush();
        this.generator.close();
    }
}
