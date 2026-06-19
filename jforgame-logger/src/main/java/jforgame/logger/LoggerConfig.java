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

    private LoggerConfig() {
    }
}
