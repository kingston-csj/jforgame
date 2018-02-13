package com.kingston.jforgame.orm.exception;

/**
 *  orm配置异常
 */
public class OrmConfigExcpetion extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OrmConfigExcpetion(Exception e) {
		super(e);
	}

	public OrmConfigExcpetion(String message) {
		super(message);
	}
}
