package jforgame.demo.game.logger;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.HashMap;
import java.util.Map;

public class LoggerBuilder {

    private static final Map<String, Logger> container = new HashMap<>();

    public static Logger getLogger(String name) {
        Logger logger = container.get(name);
        if (logger != null) {
            return logger;
        }
        synchronized (LoggerBuilder.class) {
            logger = container.get(name);
            if (logger != null) {
                return logger;
            }
            logger = build(name);
            container.put(name, logger);
        }
        return logger;
    }

    private static Logger build(String name) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%5t] %5p (%F:%L) : %m%n")
                .build();
        String logName = name.toLowerCase();
        RollingFileAppender appender = RollingFileAppender.newBuilder().setLayout(layout)
                .setName(name + "appender")
                .withFileName("logs/system/" + logName + ".log")
                .withPolicy(TimeBasedTriggeringPolicy.newBuilder().build())
                .withFilePattern("logs/system/" + logName + "/" + logName + ".log-%d{yyyy-MM-dd}")
                .setConfiguration(config)
                .build();
        appender.start();
        config.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef(name, Level.INFO, null);
        LoggerConfig loggerConfig = LoggerConfig.newBuilder().withAdditivity(false)
                .withLoggerName(name)
                .withConfig(config)
                .withLevel(Level.INFO).withRefs(new AppenderRef[]{ref}).build();

        loggerConfig.addAppender(appender, null, null);
        config.addLogger(name, loggerConfig);
        context.updateLoggers();

        return LogManager.getLogger(name);
    }


}