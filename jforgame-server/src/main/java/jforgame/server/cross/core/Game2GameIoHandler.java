package jforgame.server.cross.core;

import jforgame.server.cross.core.server.CMessageDispatcher;
import jforgame.server.cross.core.server.SCSession;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.socket.message.Message;
import jforgame.socket.mina.MinaSessionProperties;
import jforgame.socket.mina.ServerSocketIoHandler;

public class Game2GameIoHandler extends IoHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(ServerSocketIoHandler.class);

	/** 消息分发器 */
	private CMessageDispatcher messageDispatcher;
	
	private AttributeKey attrKey = new AttributeKey(MinaSessionProperties.class, "SESSION_CONTAINER");

	public Game2GameIoHandler(CMessageDispatcher messageDispatcher) {
		this.messageDispatcher = messageDispatcher;
	}

	@Override
	public void sessionCreated(IoSession session) {
		System.out.println(session.getRemoteAddress().toString());
		SCSession sessionContainer = SCSession.valueOf(session);
		session.setAttributeIfAbsent(attrKey, sessionContainer);
	}

	@Override
	public void messageReceived(IoSession session, Object data) throws Exception {
		SCSession sessionContainer = (SCSession) session.getAttribute(attrKey);
		Message message = (Message)data;
		//交由消息分发器处理
		messageDispatcher.serverDispatch(sessionContainer, message);
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.error("跨服session关闭");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("server exception", cause);
	}
}