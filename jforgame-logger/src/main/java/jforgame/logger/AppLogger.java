package jforgame.logger;

/**
 * 日志接口
 * 使用方式{@link LoggerUtil}
 */
public interface AppLogger {

    void info(String msg);

    void error(String msg, Throwable throwable);

    void error(String format, Object... arguments);
}
