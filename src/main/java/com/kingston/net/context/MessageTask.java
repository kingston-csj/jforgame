package com.kingston.net.context;

import java.lang.reflect.Method;

import com.kingston.net.Message;

public class MessageTask extends AbstractDistributeTask {
	
	private long playerId;
	/** 消息实体 */
	private Message message;
	/** 消息处理器 */
	private Object handler;
	
	private Method method;
	/** 处理器方法的参数 */
	private Object[] params;
	
	public static MessageTask valueOf(int distributeKey, Object handler,
			Method method, Object[] params) {
		MessageTask msgTask = new MessageTask();
		msgTask.distributeKey = distributeKey;
		msgTask.handler = handler;
		msgTask.method  = method;
		msgTask.params  = params;
		
		return msgTask;
	}

	@Override
	public void action() {
		try{
			method.invoke(handler, params);
		}catch(Exception e){
			
		}
		
	}

	public long getPlayerId() {
		return playerId;
	}

	public Message getMessage() {
		return message;
	}

	public Object getHandler() {
		return handler;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getParams() {
		return params;
	}
	
	@Override
	public String toString() {
		return this.getName() + "[" + handler.getClass().getName() + "@" + method.getName() + "]";
	}
	
}
