package jforgame.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger utility class
 * Logs recorded using {@link Logger#error(String)} will be redirected to the exception directory without stack trace.
 * Supports three info log categories:
 * 1. Normal log: use org.slf4j.LoggerFactory.getLogger API
 * All logs are written to the unified "app" file and roll daily.
 * 2. System log: or {@link #logSystem(String, String)}.
 * This is a semantic wrapper over normal classified logs, also rolling daily.
 * 3. Business log: use  {@link #logBusiness(String)}.
 * All business logs are written to the unified "business" file and roll hourly.
 * All parameters are formatted as json, convenient for third-party parsing.
 * Logs recorded using {@link #error(String, Exception)} will be redirected to the exception directory with stack trace.
 */
public class LoggerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class);

    /**
     * Output info‑level log message
     *
     * @param format    log message format string
     * @param arguments arguments for format placeholder
     */
    public static void info(String format, Object... arguments) {
        LOGGER.info(format, arguments);
    }

    /**
     * Log an exception at the ERROR level with an accompanying message.
     *
     * @param errMsg the message accompanying the exception
     * @param e      the exception to log
     */
    public static void error(String errMsg, Exception e) {
        LOGGER.error(errMsg, e);
    }

    public static void error(String format, Object... arguments) {
        LOGGER.error(format, arguments);
    }

    /**
     * Logs a system message.
     * System logs are written to an independent file based on loggerName and roll daily.
     */
    public static void logSystem(String loggerName, String content) {
        logInternal(loggerName, content);
    }

    /**
     * Logs a business message.
     * Business logs are written to the unified "business" file and roll hourly.
     */
    public static void logBusiness(String content) {
        logInternal(LoggerConfig.BUSINESS_LOGGER_NAME, content);
    }

    private static void logInternal(String loggerName, String content) {
        AppLogger logger = LoggerBuilder.getLogger(loggerName);
        logger.info(content);
    }

}
