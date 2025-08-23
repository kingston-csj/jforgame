package jforgame.socket.share;

/**
 * 用于适配SocketIoDispatcher接口的默认实现
 */
public class SocketIoDispatcherAdapter implements SocketIoDispatcher {
    @Override
    public void onSessionCreated(IdSession session) {

    }

    @Override
    public void dispatch(IdSession session, Object frame) {

    }

    @Override
    public void onSessionClosed(IdSession session) {

    }

    @Override
    public void exceptionCaught(IdSession session, Throwable cause) {

    }
}
