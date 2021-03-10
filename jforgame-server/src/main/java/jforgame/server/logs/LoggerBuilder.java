package jforgame.server.logs;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.TimeBasedRollingPolicy;

import java.util.HashMap;
import java.util.Map;

public class LoggerBuilder {

    private static final Map<String, Logger> container = new HashMap<>();

    private static final String LOG_PATH = "log/";

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

    public static void main(String[] args) {
        LoggerFunction.ACTIVITY.getLogger().error("hello game");
    }

    private static Logger build(String name) {
        Logger logger = Logger.getLogger(name);
        logger.removeAllAppenders();
        logger.setLevel(Level.INFO);
        logger.setAdditivity(false);

        RollingFileAppender appender = new RollingFileAppender();
        PatternLayout layout = new PatternLayout();
        String conversionPatten = "[%d] %p %t %c - %m%n";
        layout.setConversionPattern(conversionPatten);
        appender.setLayout(layout);
        appender.setEncoding("utf-8");
        appender.setAppend(true);

        TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
        String fp = LoggerBuilder.LOG_PATH + name + "/" + name + ".log.%d{yyyy-MM-dd}";
        policy.setFileNamePattern(fp);
        appender.setRollingPolicy(policy);

        appender.activateOptions();
        logger.addAppender(appender);

        container.put(name,logger);
        return logger;
    }

}
