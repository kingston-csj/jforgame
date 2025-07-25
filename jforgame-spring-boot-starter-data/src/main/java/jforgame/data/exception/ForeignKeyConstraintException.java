package jforgame.data.exception;

/**
 * 外键约束异常
 */
public class ForeignKeyConstraintException extends DataValidateException {
    public ForeignKeyConstraintException(String message) {
        super(message);
    }

    public ForeignKeyConstraintException(String message, Throwable cause) {
        super(message, cause);
    }
}