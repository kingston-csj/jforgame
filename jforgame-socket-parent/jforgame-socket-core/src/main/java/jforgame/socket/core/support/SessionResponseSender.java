package jforgame.socket.core.support;

import jforgame.socket.core.dispatch.RequestContext;
import jforgame.socket.core.dispatch.RequestResponseSender;
import jforgame.socket.core.session.IdSession;

/**
 * Default response sender, directly returns packet through current session.
 */
public class SessionResponseSender implements RequestResponseSender {

    public static final SessionResponseSender INSTANCE = new SessionResponseSender();

    @Override
    public void send(IdSession session, RequestContext requestContext) {
        session.send(requestContext.getHeader().getIndex(), requestContext.getResponse());
    }
}
