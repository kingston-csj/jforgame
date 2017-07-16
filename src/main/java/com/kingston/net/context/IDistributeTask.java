package com.kingston.net.context;

/**
 * 可分发的任务接口
 * @author kingston
 */
public interface IDistributeTask {
	
	/**
	 * 分发的工作线程索引
	 * @return
	 */
	int distributeKey();
	
	/**
	 * 获取名字
	 * @return
	 */
	String getName();
	
	/**
	 * 执行业务
	 */
	void action();
	
	
}
