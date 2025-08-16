package jforgame.orm.converter;

import javax.persistence.AttributeConverter;

/**
 * jpa的转换器本身并没有申明当{@link AttributeConverter}转换失败时抛出何种异常
 * 这里增加一个自定义异常
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
