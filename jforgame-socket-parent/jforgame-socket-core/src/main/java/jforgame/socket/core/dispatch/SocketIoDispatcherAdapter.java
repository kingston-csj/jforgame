package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * Default implementation for adapting SocketIoDispatcher interface
 */
public class SocketIoDispatcherAdapter implements SocketIoDispatcher {
    @Override
    public void onSessionCreated(IdSession session) {

    }

    @Override
    public void dispatch(IdSession session, RequestContext context) {

    }

    @Override
    public void onSessionClosed(IdSession session) {

    }

    @Override
    public void exceptionCaught(IdSession session, Throwable cause) {

    }
}
