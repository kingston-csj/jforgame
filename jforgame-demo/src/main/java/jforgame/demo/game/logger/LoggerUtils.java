package jforgame.demo.game.logger;

import org.slf4j.Logger;

public class LoggerUtils {

    public static void info(String format, Object... arguments) {
        Logger logger = LoggerSystem.MONITOR.getLogger();
        logger.info(format, arguments);
    }

    /**
     * Log an exception at the ERROR level with an accompanying message.
     *
     * @param errMsg the message accompanying the exception
     * @param e   the exception to log
     */
    public static void error(String errMsg, Exception e) {
        Logger logger = LoggerSystem.EXCEPTION.getLogger();
        logger.error("", e);
    }

    public static void error(String format, Object... arguments) {
        Logger logger = LoggerSystem.EXCEPTION.getLogger();
        logger.error(format, arguments);
    }
}