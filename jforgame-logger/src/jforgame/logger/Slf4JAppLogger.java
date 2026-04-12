package jforgame.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JAppLogger implements AppLogger {

    private final Logger delegate;

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
