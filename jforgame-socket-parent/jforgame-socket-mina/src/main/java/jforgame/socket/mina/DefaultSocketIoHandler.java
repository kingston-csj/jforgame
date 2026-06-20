package jforgame.socket.mina;

import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.RequestContext;
import jforgame.socket.core.dispatch.SocketIoDispatcher;
import jforgame.socket.core.protocol.message.RequestDataFrame;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

/**
 * Default message IO dispatcher
 */
public class DefaultSocketIoHandler extends IoHandlerAdapter {

    /**
     * Message dispatcher
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
    public void messageReceived(IoSession session, Object frame) throws Exception {
        assert frame instanceof RequestDataFrame;
        RequestDataFrame requestDataFrame = (RequestDataFrame) frame;
        IdSession userSession = (IdSession) session.getAttribute(USER_SESSION);
        // Delegate to message dispatcher for processing
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(requestDataFrame.getMessage());
        requestContext.setHeader(requestDataFrame.getHeader());
        messageDispatcher.dispatch(userSession, requestContext);
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