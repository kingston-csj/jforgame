package jforgame.orm.ddl;

/**
 * 数据库schema验证异常
 * 当数据库schema与代码定义不一致时抛出此异常
 */
public class SchemaValidationException extends RuntimeException {

    public SchemaValidationException(String message) {
        super(message);
    }

    public SchemaValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 