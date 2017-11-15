package com.kingston.orm.exception;

/**
 *  orm配置异常
 */
public class OrmConfigExcpetion extends RuntimeException {

	public OrmConfigExcpetion(Exception e) {
		super(e);
	}
	
	public OrmConfigExcpetion(String message) {
		super(message);
	}
}
