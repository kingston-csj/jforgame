package jforgame.logger;

import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志工厂，根据应用程序所使用的日志框架，选择对应的日志实现
 * 如果是log4j，则使用{@link Log4JAppLogger}
 * 如果是log4j2，则使用{@link Log4J2AppLogger}
 * 如果是logback，则使用{@link LogbackAppLogger}
 * 如果以上都不是，则使用{@link Slf4JAppLogger}，但该日志无实际日志输出，只是保证程序不报错。
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
