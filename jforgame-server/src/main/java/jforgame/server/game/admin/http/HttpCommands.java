package jforgame.server.game.admin.http;

/**
 * http command enum
 * @author kinson
 */
public final class HttpCommands {

	/** stop game server */
	public static final int CLOSE_SERVER = 1;

	public static final int QUERY_SERVER_OPEN_TIME = 2;
	/** player force offline */
	public static final int KICK_PLAYER = 3;
	/** 功能开关，线上紧急关功能修bug ^_^ */
	public static final int FUNC_SWITCH = 4;

}
