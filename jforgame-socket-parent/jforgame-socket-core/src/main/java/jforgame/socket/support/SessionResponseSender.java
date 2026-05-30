package jforgame.socket.support;

import jforgame.socket.dispatch.RequestContext;
import jforgame.socket.dispatch.RequestResponseSender;
import jforgame.socket.session.IdSession;

/**
 * 默认响应发送器，直接通过当前 session 回包。
 */
public class SessionResponseSender implements RequestResponseSender {

    public static final SessionResponseSender INSTANCE = new SessionResponseSender();

    @Override
    public void send(IdSession session, RequestContext requestContext, Object response) {
        session.send(requestContext.getHeader().getIndex(), response);
    }
}
