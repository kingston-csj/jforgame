package jforgame.orm.converter;

import javax.persistence.AttributeConverter;

/**
 * Jpa's AttributeConverter does not declare what exception to throw when conversion fails.
 * This class provides a custom exception for this situation.
 */
public class AttributeConversionException extends RuntimeException {

    public AttributeConversionException(String message) {
        super(message);
    }

    public AttributeConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttributeConversionException(Throwable cause) {
        super(cause);
    }
}
