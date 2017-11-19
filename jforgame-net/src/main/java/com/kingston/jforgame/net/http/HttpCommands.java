package com.kingston.jforgame.net.http;

/**
 * http command enum
 * @author kingston
 */
public final class HttpCommands {

	/** stop game server */
	public static final int CLOSE_SERVER = 1;

	public static final int QUERY_SERVER_OPEN_TIME = 2;
	/** player force offline */
	public static final int KICK_PLAYER = 3;

}
