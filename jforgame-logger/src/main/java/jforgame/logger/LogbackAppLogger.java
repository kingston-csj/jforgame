package jforgame.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;

/**
 * Logback implementation of {@link AppLogger}
 * Provides classified logging with separate files for each logger type
 * @see LoggerBuilder
 */
public class LogbackAppLogger implements AppLogger {

    private static final String LOG_PATTERN = "%m%n";
    private static final int MAX_HISTORY_DAYS = 15;

    private final Logger delegate;

    public LogbackAppLogger(String name) {
        LoggerBuilder.initialize();
        Logger logger = (Logger) LoggerFactory.getLogger(name);
        configure(name, logger);
        this.delegate = logger;
    }

    static void initializeDefaultConfiguration() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        synchronized (context) {
            context.reset();
            context.getStatusManager().clear();
            ConsoleAppender<ILoggingEvent> console = new ConsoleAppender<>();
            console.setContext(context);
            console.setName("Console");
            console.setEncoder(buildRootEncoder(context));
            console.start();
            Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.INFO);
            root.addAppender(console);
            root.addAppender(buildRootAppender(context, "App", LoggerConfig.LOG_PATH + "/app.log", false));
            root.addAppender(buildRootAppender(context, "Exception", LoggerConfig.LOG_PATH + "/exception/exception.log", true));
        }
    }

    private static PatternLayoutEncoder buildRootEncoder(LoggerContext context) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(LoggerConfig.DEFAULT_PATTERN);
        encoder.start();
        return encoder;
    }

    private static RollingFileAppender<ILoggingEvent> buildRootAppender(LoggerContext context, String name,
                                                                       String file, boolean errorOnly) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);
        appender.setName(name);
        appender.setFile(file);
        appender.setAppend(true);
        appender.setEncoder(buildRootEncoder(context));
        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setContext(context);
        policy.setParent(appender);
        policy.setFileNamePattern(file + ".%d{yyyy-MM-dd}");
        policy.setMaxHistory(LoggerConfig.DEFAULT_HISTORY_DAYS);
        policy.setCleanHistoryOnStart(true);
        policy.start();
        appender.setRollingPolicy(policy);
        if (errorOnly) {
            LevelFilter filter = new LevelFilter();
            filter.setContext(context);
            filter.setLevel(Level.ERROR);
            filter.setOnMatch(FilterReply.ACCEPT);
            filter.setOnMismatch(FilterReply.DENY);
            filter.start();
            appender.addFilter(filter);
        } else {
            ThresholdFilter filter = new ThresholdFilter();
            filter.setContext(context);
            filter.setLevel("INFO");
            filter.start();
            appender.addFilter(filter);
        }
        appender.start();
        return appender;
    }

    /**
     * Configures a dedicated appender for the specified logger
     * Creates a rolling file appender with time-based policy
     *
     * @param name the logger name
     * @param logger the logger instance to configure
     */
    private void configure(String name, Logger logger) {
        String appenderName = "CLASSIFY_" + name;
        synchronized (logger) {
            if (logger.getAppender(appenderName) != null) {
                return;
            }
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            String filePath = LoggerConfig.resolveFilePath(name);
            LoggerConfig.RollingPeriod rollingPeriod = LoggerConfig.resolveRollingPeriod(name);

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern(LOG_PATTERN);
            encoder.start();

            TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
            policy.setContext(context);
            policy.setFileNamePattern(rollingPeriod.log4j2FilePattern(filePath));
            policy.setMaxHistory(MAX_HISTORY_DAYS);

            RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
            appender.setContext(context);
            appender.setName(appenderName);
            appender.setAppend(true);
            appender.setFile(filePath);
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
