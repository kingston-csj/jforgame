package jforgame.demo.game.logger;


import jforgame.commons.JsonUtil;
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

public enum LoggerBusiness {

    ACTIVITY,

    ITEM,

    GOLD,


    ;

    static final Logger logger = createLogger();

    public Logger getLogger() {
        return logger;
    }

    private static Logger createLogger() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
//                .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%5t] %5p (%F:%L) : %m%n")
                .build();
        RollingFileAppender appender = RollingFileAppender.newBuilder().setLayout(layout)
                .setName("BusinessAppender")
                .withFileName("logs/business/business.log")
                .withPolicy(TimeBasedTriggeringPolicy.newBuilder().build())
                .withFilePattern("logs/business/business.log-%d{yyyy-MM-dd}")
                .setConfiguration(config)
                .build();
        appender.start();
        config.addAppender(appender);
        String name = "business";
        AppenderRef ref = AppenderRef.createAppenderRef(name, Level.INFO, null);
        LoggerConfig loggerConfig = LoggerConfig.newBuilder().withAdditivity(false)
                .withLoggerName(name)
                .withConfig(config)
                .withLevel(Level.INFO).withRefs(new AppenderRef[]{ref}).build();


        loggerConfig.addAppender(appender, Level.INFO, null);
        config.addLogger(name, loggerConfig);
        context.updateLoggers();

        return LogManager.getLogger(name);
    }

    public void info(String... args) {
        if (args.length == 0) {
            return;
        }
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("params must be odd number");
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", this.name());
        params.put("time", "" + System.currentTimeMillis());
        for (int i = 0, n = args.length / 2; i <= n; i += 2) {
            String key = args[i];
            String value = args[i + 1];
            params.put(key, value);
        }
        logger.info(JsonUtil.object2String(params));
    }

}
