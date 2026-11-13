package jforgame.logger;

/**
 * Logger interface
 * Usage: {@link LoggerUtil}
 * @since 3.5.0
 */
public interface AppLogger {

    void info(String msg);

    void error(String msg, Throwable throwable);

    void error(String format, Object... arguments);
}
