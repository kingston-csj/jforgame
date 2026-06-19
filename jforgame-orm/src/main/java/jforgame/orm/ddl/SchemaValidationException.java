package jforgame.orm.ddl;

/**
 * Database schema validation exception.
 * Thrown when database schema is inconsistent with code definitions.
 */
public class SchemaValidationException extends RuntimeException {

    public SchemaValidationException(String message) {
        super(message);
    }

    public SchemaValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 