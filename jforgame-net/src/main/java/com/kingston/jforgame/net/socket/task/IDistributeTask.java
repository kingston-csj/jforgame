package com.kingston.jforgame.net.socket.task;

/**
 * @author kingston
 */
public interface IDistributeTask extends IDistributable {

	/**
	 * name of the task
	 */
	String getName();

	/**
	 * execute logic
	 */
	void action();

}
