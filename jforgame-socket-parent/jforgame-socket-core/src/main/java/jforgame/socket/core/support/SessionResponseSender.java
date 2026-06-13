package jforgame.socket.core.support;

import jforgame.socket.core.dispatch.RequestContext;
import jforgame.socket.core.dispatch.RequestResponseSender;
import jforgame.socket.core.session.IdSession;

/**
 * 默认响应发送器，直接通过当前 session 回包。
 */
public class SessionResponseSender implements RequestResponseSender {

    public static final SessionResponseSender INSTANCE = new SessionResponseSender();

    @Override
    public void send(IdSession session, RequestContext requestContext) {
        session.send(requestContext.getHeader().getIndex(), requestContext.getResponse());
    }
}
