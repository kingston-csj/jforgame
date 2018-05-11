package com.kingston.jforgame.net.socket.task;

/**
 * @author kingston
 */
public interface Distributable {

	/**
	 * distribute key of logic thread pool
	 */
	int distributeKey();

}
