package com.kingston.net.context;

/**
 * 线程分发
 * @author kingston
 */
public interface IDistributable {
	
	/**
	 * 分发的工作线程索引
	 * @return
	 */
	int distributeKey();

}
