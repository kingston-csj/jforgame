package jforgame.socket.mina.support;

import jforgame.socket.share.IdSession;
import jforgame.socket.mina.MSession;
import jforgame.socket.share.SocketIoDispatcher;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kinson
 */
public class DefaultSocketIoHandler extends IoHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger("socketserver");

	/** 消息分发器 */
	private SocketIoDispatcher messageDispatcher;

	private final AttributeKey USER_SESSION = new AttributeKey(DefaultSocketIoHandler.class, "GameSession");


	public DefaultSocketIoHandler(SocketIoDispatcher messageDispatcher) {
		this.messageDispatcher = messageDispatcher;
	}

	@Override
	public void sessionCreated(IoSession session) {
		IdSession userSession = new MSession(session);
		session.setAttribute(USER_SESSION,
				userSession);
		messageDispatcher.onSessionCreated(userSession);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		IdSession userSession = (IdSession) session.getAttribute(USER_SESSION);
		//交由消息分发器处理
		messageDispatcher.dispatch(userSession, message);
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		IdSession userSession = (IdSession) session.getAttribute(USER_SESSION);
		messageDispatcher.onSessionClosed(userSession);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("server exception", cause);
		IdSession userSession = (IdSession) session.getAttribute(USER_SESSION);
		messageDispatcher.exceptionCaught(userSession, cause);
	}
}