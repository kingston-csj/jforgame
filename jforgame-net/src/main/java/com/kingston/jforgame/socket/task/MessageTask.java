package com.kingston.jforgame.socket.task;

import java.lang.reflect.Method;

import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.socket.message.Message;

/**
 * when server receives a message, wrapped it into a MessageTask,
 * and add it to target message consumer task queue
 * @author kingston
 */
public class MessageTask implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(MessageTask.class);

	private IdSession session;

	/** message controller */
	private Object handler;
	/** target method of the controller */
	private Method method;
	/**arguments passed to the method */
	private Object[] params;

	public static MessageTask valueOf(IdSession session,Object handler,
			Method method, Object[] params) {
		MessageTask msgTask = new MessageTask();
		msgTask.session = session;
		msgTask.handler = handler;
		msgTask.method  = method;
		msgTask.params  = params;

		return msgTask;
	}

	@Override
	public void run() {
		try{
			Object response = method.invoke(handler, params);
			if (response != null) {
				session.sendPacket((Message) response);
			}
		}catch(Exception e){
			logger.error("message task execute failed ", e);
		}
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
		return  "[" + handler.getClass().getName() + "@" + method.getName() + "]";
	}

}
