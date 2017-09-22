package com.kingston.net.context;

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
