package jforgame.logger;

/**
 * Logger configuration constants
 * Log path can be customized via JVM property: -Djforgame.log.path=/path/to/logs
 */
final class LoggerConfig {

    /**
     * Root log directory path
     * Default value is "./logs", can be overridden by system property "jforgame.log.path"
     */
    public static final String LOG_PATH = System.getProperty("jforgame.log.path", "./logs");

    static final String DEFAULT_PATTERN = "%d{yyyy-MM-dd HH:mm:ss} %-4r [%t] %-5p %c{35} - %m%n";
    static final int DEFAULT_HISTORY_DAYS = 30;

    static final String BUSINESS_LOGGER_NAME = "business";

    static boolean isBusinessLogger(String loggerName) {
        return BUSINESS_LOGGER_NAME.equalsIgnoreCase(loggerName);
    }

    static RollingPeriod resolveRollingPeriod(String loggerName) {
        return isBusinessLogger(loggerName) ? RollingPeriod.HOURLY : RollingPeriod.DAILY;
    }

    static String resolveFilePath(String loggerName) {
        String fileName = loggerName.toLowerCase();
        return LOG_PATH + "/" + fileName + "/" + fileName + ".log";
    }

    enum RollingPeriod {
        DAILY("yyyyMMdd"),
        HOURLY("yyyyMMddHH");

        private final String datePattern;

        RollingPeriod(String datePattern) {
            this.datePattern = datePattern;
        }

        String log4j2FilePattern(String filePath) {
            return filePath + ".%d{" + datePattern + "}";
        }

        String log4jDatePattern() {
            return "'.'" + datePattern;
        }
    }

    private LoggerConfig() {
    }
}
