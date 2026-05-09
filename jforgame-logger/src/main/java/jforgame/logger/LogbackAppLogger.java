package jforgame.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.LoggerFactory;

public class LogbackAppLogger implements AppLogger {

    private static final String LOG_PATH = LoggerConfig.LOG_PATH;
    private static final String LOG_PATTERN = "%m%n";
    private static final int MAX_HISTORY_DAYS = 15;

    private final Logger delegate;

    public LogbackAppLogger(String name) {
        Logger logger = (Logger) LoggerFactory.getLogger(name);
        configure(name, logger);
        this.delegate = logger;
    }

    private void configure(String name, Logger logger) {
        String appenderName = "CLASSIFY_" + name;
        synchronized (logger) {
            if (logger.getAppender(appenderName) != null) {
                return;
            }
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            String fileName = name.toLowerCase();
            String rollingPattern = LOG_PATH + "/" + fileName + "/" + fileName + ".log.%d{yyyy-MM-dd}";

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern(LOG_PATTERN);
            encoder.start();

            TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
            policy.setContext(context);
            policy.setFileNamePattern(rollingPattern);
            policy.setMaxHistory(MAX_HISTORY_DAYS);

            RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
            appender.setContext(context);
            appender.setName(appenderName);
            appender.setAppend(true);
            appender.setEncoder(encoder);
            appender.setRollingPolicy(policy);
            policy.setParent(appender);
            policy.start();
            appender.start();

            logger.setAdditive(false);
            logger.setLevel(Level.INFO);
            logger.addAppender(appender);
        }
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
