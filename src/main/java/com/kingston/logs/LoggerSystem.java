package com.kingston.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LoggerSystem {
	
	/** 异常日志 */
	EXCEPTION,
//	/** 网关日志 */
//	NET,
	
	;
	
	public Logger getLogger() {
		return LoggerFactory.getLogger(this.name());
	}

}
