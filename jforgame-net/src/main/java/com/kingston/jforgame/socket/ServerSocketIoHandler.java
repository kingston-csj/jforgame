package com.kingston.jforgame.socket;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.socket.message.IMessageDispatcher;
import com.kingston.jforgame.socket.message.Message;
import com.kingston.jforgame.socket.session.SessionManager;
import com.kingston.jforgame.socket.session.SessionProperties;

/**
 * @author kingston
 */
public class ServerSocketIoHandler extends IoHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(ServerSocketIoHandler.class);

	/** 消息分发器 */
	private IMessageDispatcher messageDispatcher;

	public ServerSocketIoHandler(IMessageDispatcher messageDispatcher) {
		this.messageDispatcher = messageDispatcher;
	}

	@Override
	public void sessionCreated(IoSession session) {
		System.out.println(session.getRemoteAddress().toString());
		session.setAttributeIfAbsent(SessionProperties.DISTRIBUTE_KEY,
				SessionManager.INSTANCE.getNextDistributeKey());
	}

	@Override
	public void messageReceived(IoSession session, Object data) throws Exception {
		Message message = (Message)data;
		//交由消息分发器处理
		messageDispatcher.dispatch(session, message);
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		messageDispatcher.onSessionClosed(session);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("server exception", cause);
	}
}