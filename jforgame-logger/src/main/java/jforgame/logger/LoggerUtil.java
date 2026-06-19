package jforgame.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger utility class
 * Logs recorded using {@link Logger#error(String)} will be redirected to the exception directory without stack trace.
 * Logs recorded using {@link #info} are treated as operational logs, output to corresponding directories based on log type.
 * All parameters are formatted as key-value pairs separated by '|', convenient for third-party parsing.
 * Logs recorded using {@link #error(String, Exception)} will be redirected to the exception directory with stack trace.
 */
public class LoggerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class);

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
     * Logs info message with automatic classification by logger type, each type has its own directory
     *
     * @param logger the log type name
     * @param args   parameters, must be even number (key-value pairs)
     */
    public static void info(String logger, Object... args) {
        logInternal(logger, args);
    }

    private static void logInternal(String loggerName, Object... args) {
        if (args.length == 0) {
            return;
        }
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("args.length must be even");
        }
        AppLogger logger = LoggerBuilder.getLogger(loggerName);
        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = args.length; i < n; i += 2) {
            String key = String.valueOf(args[i]);
            Object value = args[i + 1];
            sb.append(key).append("|")
                    .append(value).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        logger.info(sb.toString());
    }

}
