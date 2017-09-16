package com.kingston.net.context;

/**
 * 可分发的任务接口
 * @author kingston
 */
public interface IDistributeTask extends IDistributable {
	
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
