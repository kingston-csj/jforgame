package jforgame.logger;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.slf4j.helpers.MessageFormatter;

import java.util.Enumeration;

public class Log4JAppLogger implements AppLogger {

    private static final String LOG_PATH = LoggerConfig.LOG_PATH;
    private static final String LOG_PATTERN = "%m%n";
    private static volatile boolean ROOT_APPENDER_READY = false;

    private final Logger delegate;

    public Log4JAppLogger(String name) {
        ensureRootAppender();
        Logger logger = Logger.getLogger(name);
        configure(name, logger);
        this.delegate = logger;
    }

    private static void ensureRootAppender() {
        if (ROOT_APPENDER_READY) {
            return;
        }
        synchronized (Log4JAppLogger.class) {
            if (ROOT_APPENDER_READY) {
                return;
            }
            Logger rootLogger = Logger.getRootLogger();
            Enumeration<?> appenders = rootLogger.getAllAppenders();
            if (!appenders.hasMoreElements()) {
                DailyRollingFileAppender appender = new DailyRollingFileAppender();
                appender.setName("APP_MAIN");
                appender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{35} - %m%n"));
                appender.setFile(LOG_PATH + "/app.log");
                appender.setDatePattern("'.'yyyy-MM-dd");
                appender.setAppend(true);
                appender.activateOptions();
                rootLogger.setLevel(Level.INFO);
                rootLogger.addAppender(appender);
            }
            ROOT_APPENDER_READY = true;
        }
    }

    private synchronized void configure(String name, Logger logger) {
        String appenderName = "CLASSIFY_" + name;
        if (logger.getAppender(appenderName) != null) {
            return;
        }
        String fileName = name.toLowerCase();
        String filePath = LOG_PATH + "/" + fileName + "/" + fileName + ".log";
        DailyRollingFileAppender appender = new DailyRollingFileAppender();
        appender.setName(appenderName);
        appender.setLayout(new PatternLayout(LOG_PATTERN));
        appender.setFile(filePath);
        appender.setDatePattern("'.'yyyy-MM-dd");
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
