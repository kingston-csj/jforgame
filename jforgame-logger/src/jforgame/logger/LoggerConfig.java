package jforgame.logger;

final class LoggerConfig {

    public static final String LOG_PATH = System.getProperty("jforgame.log.path", "./logs");

    private LoggerConfig() {
    }
}
