package jforgame.data.exception;

/**
 * Configuration validation exception
 */
public class DataValidateException extends Exception {
    public DataValidateException(String message) {
        super(message);
    }

    public DataValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}