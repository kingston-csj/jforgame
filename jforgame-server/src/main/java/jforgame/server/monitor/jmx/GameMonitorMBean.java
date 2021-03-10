package jforgame.server.monitor.jmx;

import javax.management.MXBean;

/**
 * JMX游戏管理
 * @author kinson_csj
 */
@MXBean
public interface GameMonitorMBean {

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
	  * 网关消息统计
     * @return
     */
    String getMessageStatistics();

	/**
	 * execute JavaScript code
	 * @param jsCode
	 * @return
	 */
	String execJavaScript(String jsCode);

	/**
	 * execute groovy code
	 * @param groovyCode
	 * @return
	 */
	String execGroovyScript(String groovyCode);


}
