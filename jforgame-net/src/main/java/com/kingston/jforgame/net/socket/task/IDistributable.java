package com.kingston.jforgame.net.socket.task;

/**
 * @author kingston
 */
public interface IDistributable {

	/**
	 * distribute key of logic thread pool
	 */
	int distributeKey();

}
