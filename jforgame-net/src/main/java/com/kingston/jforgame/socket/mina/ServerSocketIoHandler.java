package com.kingston.jforgame.socket.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.message.IMessageDispatcher;
import com.kingston.jforgame.socket.message.Message;
import com.kingston.jforgame.socket.session.MinaSessionProperties;

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
		IdSession userSession = new MinaSession(session);
		System.out.println(session.getRemoteAddress().toString());
		session.setAttribute(MinaSessionProperties.UserSession,
				userSession);
		messageDispatcher.onSessionCreated(userSession);
	}

	@Override
	public void messageReceived(IoSession session, Object data) throws Exception {
		Message message = (Message)data;
		IdSession userSession = (IdSession) session.getAttribute(MinaSessionProperties.UserSession);
		//交由消息分发器处理
		messageDispatcher.dispatch(userSession, message);
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		IdSession userSession = (IdSession) session.getAttribute(MinaSessionProperties.UserSession);
		messageDispatcher.onSessionClosed(userSession);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("server exception", cause);
	}
}