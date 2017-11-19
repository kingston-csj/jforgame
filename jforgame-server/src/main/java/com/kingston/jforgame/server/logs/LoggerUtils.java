package com.kingston.jforgame.server.logs;

import org.slf4j.Logger;

public class LoggerUtils {

	/**
	 * Log an exception at the ERROR level with an
	 * accompanying message.
	 *
	 * @param msg the message accompanying the exception
	 * @param t   the exception to log
	 */
	public static void error(String errMsg, Exception e) {
		Logger logger = LoggerSystem.EXCEPTION.getLogger(); 
		logger.error("", e);  
	}
	
	/**
	 * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
	public static void error(String format, Object... arguments) {
		Logger logger = LoggerSystem.EXCEPTION.getLogger(); 
		logger.error(format, arguments);  
	}
}
