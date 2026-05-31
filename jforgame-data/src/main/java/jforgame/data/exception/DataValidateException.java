package jforgame.data.exception;

/**
 * 配置校验异常
 */
public class DataValidateException extends Exception {
    public DataValidateException(String message) {
        super(message);
    }

    public DataValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}