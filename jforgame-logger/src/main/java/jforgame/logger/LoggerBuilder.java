package jforgame.logger;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logger factory that selects the corresponding logger implementation based on the application's logging framework.
 * If using log4j, uses {@link Log4JAppLogger}
 * If using log4j2, uses {@link Log4J2AppLogger}
 * If using logback, uses {@link LogbackAppLogger}
 * If none of the above, uses {@link Slf4JAppLogger} (no actual log output, just ensures the program doesn't error)
 * If you use springboot , please use System.setProperty("org.springframework.boot.logging.LoggingSystem", "none") to disable its default logging
 */
public class LoggerBuilder {

    private static final Map<String, AppLogger> CONTAINER = new ConcurrentHashMap<>();
    private static final LogType LOG_TYPE = detectLogType();
    private static final Constructor<? extends AppLogger> LOGGER_CONSTRUCTOR = initializeLogger();

    private static Constructor<? extends AppLogger> initializeLogger() {
        try {
            // Resolve only the selected adapter; other backends can be absent from the classpath.
            Class<? extends AppLogger> loggerClass = Class.forName(LOG_TYPE.loggerClassName,
                    true, LoggerBuilder.class.getClassLoader()).asSubclass(AppLogger.class);
            Constructor<? extends AppLogger> constructor = loggerClass.getConstructor(String.class);
            if (LOG_TYPE != LogType.SLF4J) {
                loggerClass.getDeclaredMethod("initializeDefaultConfiguration").invoke(null);
            }
            return constructor;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot initialize logging adapter: " + LOG_TYPE.loggerClassName,
                    e instanceof InvocationTargetException ? e.getCause() : e);
        }
    }

    /**
     * Initializes the detected framework once. Call before using SLF4J directly at application startup.
     * LoggerUtil and classified loggers invoke this automatically.
     */
    public static void initialize() {
        // Invoking this method guarantees completion of the static initializer.
    }

    public static AppLogger getLogger(String name) {
        return CONTAINER.computeIfAbsent(name, LoggerBuilder::build);
    }

    private static AppLogger build(String name) {
        try {
            return LOGGER_CONSTRUCTOR.newInstance(name);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create logger '" + name + "' using " + LOG_TYPE.loggerClassName,
                    e instanceof InvocationTargetException ? e.getCause() : e);
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
        LOGBACK("jforgame.logger.LogbackAppLogger"),
        LOG4J("jforgame.logger.Log4JAppLogger"),
        LOG4J2("jforgame.logger.Log4J2AppLogger"),
        SLF4J("jforgame.logger.Slf4JAppLogger");

        private final String loggerClassName;

        LogType(String loggerClassName) {
            this.loggerClassName = loggerClassName;
        }
    }

}
