package jforgame.orm.core;

/**
 * Orm configuration exception
 */
public class OrmConfigException extends RuntimeException {

    public OrmConfigException(Exception e) {
        super(e);
    }

    public OrmConfigException(String message) {
        super(message);
    }
}
