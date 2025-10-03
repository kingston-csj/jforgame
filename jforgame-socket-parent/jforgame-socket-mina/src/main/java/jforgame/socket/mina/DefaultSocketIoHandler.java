package jforgame.socket.mina;

import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

/**
 * 默认消息io分发器
 */
public class DefaultSocketIoHandler extends IoHandlerAdapter {

    /**
     * 消息分发器
     */
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
        IdSession userSession = (IdSession) session.getAttribute(USER_SESSION);
        messageDispatcher.exceptionCaught(userSession, cause);
    }
}