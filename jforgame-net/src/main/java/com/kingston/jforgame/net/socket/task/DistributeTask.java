package com.kingston.jforgame.net.socket.task;

/**
 * @author kingston
 */
public interface DistributeTask extends Distributable {

	/**
	 * name of the task
	 */
	String getName();

	/**
	 * execute logic
	 */
	void action();

}
