package jforgame.data.exception;

/**
 * Foreign key constraint exception
 */
public class ForeignKeyConstraintException extends DataValidateException {
    public ForeignKeyConstraintException(String message) {
        super(message);
    }

    public ForeignKeyConstraintException(String message, Throwable cause) {
        super(message, cause);
    }
}