package jforgame.logger;

import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logger factory that selects the corresponding logger implementation based on the application's logging framework.
 * If using log4j, uses {@link Log4JAppLogger}
 * If using log4j2, uses {@link Log4J2AppLogger}
 * If using logback, uses {@link LogbackAppLogger}
 * If none of the above, uses {@link Slf4JAppLogger} (no actual log output, just ensures the program doesn't error)
 */
public class LoggerBuilder {

    private static final Map<String, AppLogger> CONTAINER = new ConcurrentHashMap<>();
    private static final LogType LOG_TYPE = detectLogType();

    public static AppLogger getLogger(String name) {
        return CONTAINER.computeIfAbsent(name, LoggerBuilder::build);
    }

    private static AppLogger build(String name) {
        switch (LOG_TYPE) {
            case LOG4J2:
                return new Log4J2AppLogger(name);
            case LOG4J:
                return new Log4JAppLogger(name);
            case LOGBACK:
                return new LogbackAppLogger(name);
            default:
                return new Slf4JAppLogger(name);
        }
    }

    private static LogType detectLogType() {
        String factoryClassName = LoggerFactory.getILoggerFactory().getClass().getName();
        if (factoryClassName.startsWith("ch.qos.logback")) {
            return LogType.LOGBACK;
        }
        if (factoryClassName.startsWith("org.apache.logging.slf4j")) {
            return LogType.LOG4J2;
        }
        if (factoryClassName.startsWith("org.slf4j.impl.Log4j")) {
            return LogType.LOG4J;
        }
        return LogType.SLF4J;
    }

    public enum LogType {
        LOGBACK,
        LOG4J,
        LOG4J2,
        SLF4J
    }

}
