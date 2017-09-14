package com.kingston.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LoggerSystem {

	/** 异常日志 */
	EXCEPTION,
	/** 后台日志 */
	HTTP_COMMAND,
	/** job定时任务 */
	CRON_JOB;

	;

	public Logger getLogger() {
		return LoggerFactory.getLogger(this.name());
	}

}
