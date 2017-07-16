package com.kingston.net.context;


public abstract class AbstractDistributeTask implements IDistributeTask{

	/** 消息分发器的索引 */
	protected int distributeKey;
	
	/** 业务开始执行的毫秒数 */
	private long startMillis;
	
	/** 业务结束执行的毫秒数 */
	private long endMillis;
	
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public int distributeKey() {
		return distributeKey;
	}

	public long getStartMillis() {
		return startMillis;
	}

	public void markStartMillis() {
		this.startMillis = System.currentTimeMillis();
	}

	public long getEndMillis() {
		return endMillis;
	}

	public void markEndMillis() {
		this.endMillis = System.currentTimeMillis();
	}
	
}
