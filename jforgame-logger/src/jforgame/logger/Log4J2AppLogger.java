package jforgame.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class Log4J2AppLogger implements AppLogger {

    private static final String LOG_PATH = jforgame.logger.LoggerConfig.LOG_PATH;
    private static final String LOG_PATTERN = "%m%n";
    private static volatile boolean ROOT_APPENDER_READY = false;

    private final Logger delegate;

    public Log4J2AppLogger(String name) {
        ensureRootAppender();
        configure(name);
        this.delegate = LogManager.getLogger(name);
    }

    private static void ensureRootAppender() {
        if (ROOT_APPENDER_READY) {
            return;
        }
        synchronized (Log4J2AppLogger.class) {
            if (ROOT_APPENDER_READY) {
                return;
            }
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();
            LoggerConfig rootLogger = config.getRootLogger();
            if (rootLogger.getAppenders().isEmpty()) {
                PatternLayout layout = PatternLayout.newBuilder()
                        .withConfiguration(config)
                        .withPattern("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{35} - %m%n")
                        .build();
                TimeBasedTriggeringPolicy policy = TimeBasedTriggeringPolicy.newBuilder()
                        .withInterval(1)
                        .withModulate(true)
                        .build();
                RollingFileAppender appender = RollingFileAppender.newBuilder()
                        .setConfiguration(config)
                        .withName("APP_MAIN")
                        .withFileName(LOG_PATH + "/app.log")
                        .withFilePattern(LOG_PATH + "/app.log.%d{yyyy-MM-dd}")
                        .withPolicy(policy)
                        .withAppend(true)
                        .setLayout(layout)
                        .build();
                appender.start();
                config.addAppender(appender);
                rootLogger.addAppender(appender, Level.INFO, null);
                rootLogger.setLevel(Level.INFO);
                context.updateLoggers();
            }
            ROOT_APPENDER_READY = true;
        }
    }

    private void configure(String name) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        synchronized (context) {
            Configuration config = context.getConfiguration();
            String appenderName = "CLASSIFY_" + name;
            String fileName = name.toLowerCase();
            String filePath = LOG_PATH + "/" + fileName + "/" + fileName + ".log";
            String filePattern = filePath + ".%d{yyyy-MM-dd}";

            if (config.getAppender(appenderName) == null) {
                PatternLayout layout = PatternLayout.newBuilder()
                        .withConfiguration(config)
                        .withPattern(LOG_PATTERN)
                        .build();
                TimeBasedTriggeringPolicy policy = TimeBasedTriggeringPolicy.newBuilder()
                        .withInterval(1)
                        .withModulate(true)
                        .build();
                RollingFileAppender appender = RollingFileAppender.newBuilder()
                        .setConfiguration(config)
                        .withName(appenderName)
                        .withFileName(filePath)
                        .withFilePattern(filePattern)
                        .withPolicy(policy)
                        .withAppend(true)
                        .setLayout(layout)
                        .build();
                appender.start();
                config.addAppender(appender);
            }

            LoggerConfig loggerConfig = config.getLoggerConfig(name);
            if (!name.equals(loggerConfig.getName())) {
                loggerConfig = new LoggerConfig(name, Level.INFO, false);
                config.addLogger(name, loggerConfig);
            }
            if (!loggerConfig.getAppenders().containsKey(appenderName)) {
                loggerConfig.addAppender(config.getAppender(appenderName), Level.INFO, null);
            }
            loggerConfig.setLevel(Level.INFO);
            loggerConfig.setAdditive(false);
            context.updateLoggers();
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
