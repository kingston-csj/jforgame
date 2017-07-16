package com.kingston.listener;

public @interface EventHandler {
	
	/** 绑定的世界类型列表 */
	public EventType[] value();
	
}
