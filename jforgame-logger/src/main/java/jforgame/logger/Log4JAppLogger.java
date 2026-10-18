package jforgame.logger;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.varia.LevelMatchFilter;
import org.apache.log4j.varia.DenyAllFilter;
import org.slf4j.helpers.MessageFormatter;

/**
 * Log4j (1.x) implementation of {@link AppLogger}
 * Provides classified logging with separate files for each logger type
 * DEPRECATED: Apache Log4j 1.2 has been EOL since August 2015 with unresolved security risks.
 * Do not use this class for new development. Migrate to Log4j 2 at the earliest opportunity.
 * @see Log4J2AppLogger
 */
public class Log4JAppLogger implements AppLogger {

    private static final String LOG_PATTERN = "%m%n";

    private final Logger delegate;

    public Log4JAppLogger(String name) {
        LoggerBuilder.initialize();
        Logger logger = Logger.getLogger(name);
        configure(name, logger);
        this.delegate = logger;
    }

    static void initializeDefaultConfiguration() {
        Logger rootLogger = Logger.getRootLogger();
        synchronized (rootLogger) {
            org.apache.log4j.LogManager.resetConfiguration();
            ConsoleAppender console = new ConsoleAppender(new PatternLayout(LoggerConfig.DEFAULT_PATTERN),
                    ConsoleAppender.SYSTEM_OUT);
            console.setName("Console");
            rootLogger.setLevel(Level.INFO);
            rootLogger.addAppender(console);
            rootLogger.addAppender(buildRootAppender("App", LoggerConfig.LOG_PATH + "/app.log", false));
            rootLogger.addAppender(buildRootAppender("Exception", LoggerConfig.LOG_PATH + "/exception/exception.log", true));
        }
    }

    private static RetentionDailyRollingFileAppender buildRootAppender(String name, String file, boolean errorOnly) {
        RetentionDailyRollingFileAppender appender = new RetentionDailyRollingFileAppender();
        appender.setName(name);
        appender.setFile(file);
        appender.setAppend(true);
        appender.setDatePattern("'.'yyyy-MM-dd");
        appender.setMaxHistory(LoggerConfig.DEFAULT_HISTORY_DAYS);
        appender.setThreshold(errorOnly ? Level.ERROR : Level.INFO);
        appender.setLayout(new PatternLayout(LoggerConfig.DEFAULT_PATTERN));
        if (errorOnly) {
            LevelMatchFilter filter = new LevelMatchFilter();
            filter.setLevelToMatch("ERROR");
            filter.setAcceptOnMatch(true);
            appender.addFilter(filter);
            appender.addFilter(new DenyAllFilter());
        }
        appender.activateOptions();
        return appender;
    }

    /**
     * Configures a dedicated appender for the specified logger
     * Creates a daily rolling file appender
     *
     * @param name   the logger name
     * @param logger the logger instance to configure
     */
    private synchronized void configure(String name, Logger logger) {
        String appenderName = "CLASSIFY_" + name;
        if (logger.getAppender(appenderName) != null) {
            return;
        }
        String filePath = LoggerConfig.resolveFilePath(name);
        LoggerConfig.RollingPeriod rollingPeriod = LoggerConfig.resolveRollingPeriod(name);
        DailyRollingFileAppender appender = new DailyRollingFileAppender();
        appender.setName(appenderName);
        appender.setLayout(new PatternLayout(LOG_PATTERN));
        appender.setFile(filePath);
        appender.setDatePattern(rollingPeriod.log4jDatePattern());
        appender.setAppend(true);
        appender.activateOptions();

        logger.setAdditivity(false);
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);
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
        if (arguments == null || arguments.length == 0) {
            delegate.error(format);
            return;
        }
        delegate.error(MessageFormatter.arrayFormat(format, arguments).getMessage());
    }
}
