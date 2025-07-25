package jforgame.data.exception;

/**
 * 外键约束异常
 */
public class DataValidateException extends Exception {
    public DataValidateException(String message) {
        super(message);
    }

    public DataValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}