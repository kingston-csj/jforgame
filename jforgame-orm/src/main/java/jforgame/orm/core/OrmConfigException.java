package jforgame.orm.core;

/**
 * orm配置异常
 */
public class OrmConfigException extends RuntimeException {

    public OrmConfigException(Exception e) {
        super(e);
    }

    public OrmConfigException(String message) {
        super(message);
    }
}
