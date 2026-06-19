package jforgame.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J fallback implementation of {@link AppLogger}
 * Used when no specific logging framework (Log4j, Log4j2, Logback) is detected
 * This implementation delegates to SLF4J but may not produce actual log output
 * depending on the underlying binding
 */
public class Slf4JAppLogger implements AppLogger {

    private final Logger delegate;

    /**
     * Creates a new SLF4J logger with the specified name
     *
     * @param name the logger name
     */
    public Slf4JAppLogger(String name) {
        this.delegate = LoggerFactory.getLogger(name);
    }

    @Override
    public void info(String msg) {
        delegate.info(msg);
    }

    @Override
    public void error(String msg, Throwable throwable) {
        delegate.error(msg, throwable);
    }

    @Override
    public void error(String format, Object... arguments) {
        delegate.error(format, arguments);
    }
}
