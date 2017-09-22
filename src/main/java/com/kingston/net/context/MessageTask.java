package com.kingston.net.context;

import java.lang.reflect.Method;

import com.kingston.logs.LoggerUtils;
import com.kingston.net.Message;

/**
 * when server receives a message, wrapped it into a MessageTask,
 * and put it to target message consumer task
 * @author kingston
 */
public class MessageTask extends AbstractDistributeTask {

	/** owner playerId */
	private long playerId;
	/** io message content */
	private Message message;
	/** message controller */
	private Object handler;
	/** target method of the controller */
	private Method method;
	/**arguments passed to the method */
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
			LoggerUtils.error("message task execute failed ", e);
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
