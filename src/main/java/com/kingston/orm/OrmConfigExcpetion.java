package com.kingston.orm;

/**
 *  orm配置异常
 */
public class OrmConfigExcpetion extends RuntimeException {

	private static final long serialVersionUID = 1788051162447455031L;

	public OrmConfigExcpetion(Exception e) {
		super(e);
	}
	
	public OrmConfigExcpetion(String message) {
		super(message);
	}
}
