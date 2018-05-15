package com.kingston.jforgame.socket.task;

/**
 * @author kingston
 */
public interface Distributable {

	/**
	 * distribute key of logic thread pool
	 */
	int distributeKey();

}
