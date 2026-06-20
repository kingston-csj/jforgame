package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * Message dispatcher, applicable for both server and client
 */
public interface SocketIoDispatcher {

    /**
     * Called when session is created
     *
     * @param session socket session
     */
    void onSessionCreated(IdSession session);

    /**
     * Message dispatch
     *
     * @param session socket session
     * @param context request message context
     */
    void dispatch(IdSession session, RequestContext context);

    /**
     * Called when session is closed
     *
     * @param session socket session
     */
    void onSessionClosed(IdSession session);

    /**
     * Called when session exception occurs
     *
     * @param session socket session
     * @param cause   exception
     */
    void exceptionCaught(IdSession session, Throwable cause);
}
