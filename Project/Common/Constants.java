/**
 * UCID: lap5
 * Date: 2025-11-23
 * Summary: Global constants used throughout the application.
 */
package Common;

public abstract class Constants {
    public static final long DEFAULT_CLIENT_ID = -1;
    public static final String COMMAND_TRIGGER = "/";
    public static final String CREATE_ROOM = "createroom";
    public static final String JOIN_ROOM = "joinroom";

    public static final int ROUND_TIME_MS = 30000; // 30 seconds

    // Game States
    public static final String GAME_STATE_LOBBY = "LOBBY";
    public static final String GAME_STATE_READY = "READY";
    public static final String GAME_STATE_CHOOSING = "CHOOSING";
    public static final String GAME_STATE_ROUND_END = "ROUND_END";
    public static final String GAME_STATE_GAME_OVER = "GAME_OVER";

    // RPS Choices
    public static final String ROCK = "R";
    public static final String PAPER = "P";
    public static final String SCISSORS = "S";
}
