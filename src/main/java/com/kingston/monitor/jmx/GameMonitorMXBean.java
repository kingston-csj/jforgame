package com.kingston.monitor.jmx;

public interface GameMonitorMXBean {

	/**
	 * total online players count
	 * @return
	 */
	int getOnlinePlayerSum();

	/**
	 * print server state detail,
	 * including memory, thread, buff
	 * @return
	 */
	String printServerState();

	/**
	 * execute JavaScript code
	 * @param jsCode
	 * @return
	 */
	String execJavascript(String jsCode);


}
