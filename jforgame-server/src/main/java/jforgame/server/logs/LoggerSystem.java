package jforgame.server.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LoggerSystem {

	/** system exception */
	EXCEPTION,
	/** http admin */
	HTTP_COMMAND,
	/** job scheduler */
	CRON_JOB,
	/** server monitor */
	MONITOR,

	;

	public Logger getLogger() {
		return LoggerFactory.getLogger(this.name());
	}

}
