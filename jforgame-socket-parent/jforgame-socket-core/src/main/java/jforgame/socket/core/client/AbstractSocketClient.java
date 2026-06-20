package jforgame.socket.core.client;

import jforgame.codec.MessageCodec;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.SocketIoDispatcher;
import jforgame.socket.core.dispatch.SocketIoDispatcherAdapter;
import jforgame.socket.core.protocol.message.MessageFactory;

/**
 * Abstract socket client
 * Provides more connection details and parameter configuration
 */
public abstract class AbstractSocketClient implements SocketClient {

    /**
     * A no-op io dispatcher, can be used when client doesn't need to capture various IO events of SocketIoDispatcher
     */
    public static final SocketIoDispatcher EMPTY_DISPATCHER = new SocketIoDispatcherAdapter();

    protected SocketIoDispatcher ioDispatcher = EMPTY_DISPATCHER;

    protected MessageFactory messageFactory;

    protected MessageCodec messageCodec;

    /**
     * target socket ip address
     */
    protected HostAndPort targetAddress;

    protected IdSession session;

    public SocketIoDispatcher getIoDispatcher() {
        return ioDispatcher;
    }

    public void setIoDispatcher(SocketIoDispatcher ioDispatcher) {
        this.ioDispatcher = ioDispatcher;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public MessageCodec getMessageCodec() {
        return messageCodec;
    }

    public void setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
    }

    public HostAndPort getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(HostAndPort targetAddress) {
        this.targetAddress = targetAddress;
    }

    @Override
    public IdSession getSession() {
        return session;
    }

}
