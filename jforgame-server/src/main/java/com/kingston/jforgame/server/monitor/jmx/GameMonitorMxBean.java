package com.kingston.jforgame.server.monitor.jmx;

/**
 * JMX游戏管理
 * @author kingston_csj
 */
public interface GameMonitorMxBean {

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
