package jforgame.logger;

/**
 * Logger interface
 * Usage: {@link LoggerUtil}
 */
public interface AppLogger {

    void info(String msg);

    void error(String msg, Throwable throwable);

    void error(String format, Object... arguments);
}
