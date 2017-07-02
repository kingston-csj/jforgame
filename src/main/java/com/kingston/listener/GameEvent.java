package com.kingston.listener;

/**
 * 监听器监听的事件抽象类
 */
public abstract class GameEvent {
	
	/** 创建时间 */
	private long createTime;
	/** 事件类型 */
	private final EventType eventType;
	
	public GameEvent(EventType evtType) {
		this.createTime = System.currentTimeMillis();
		this.eventType  = evtType;
	}
	
	public long getCreateTime() {
		return this.createTime;
	}
	
	public EventType getEventType() {
		return this.eventType;
	}

}
