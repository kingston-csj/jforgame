package jforgame.orm;

/**
 *  orm配置异常
 */
public class OrmConfigException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OrmConfigException(Exception e) {
		super(e);
	}

	public OrmConfigException(String message) {
		super(message);
	}
}
