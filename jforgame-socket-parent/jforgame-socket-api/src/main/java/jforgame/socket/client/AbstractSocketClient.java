package jforgame.socket.client;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;

public abstract class AbstractSocketClient implements SocketClient {

    protected SocketIoDispatcher messageDispatcher;

    protected MessageFactory messageFactory;

    protected MessageCodec messageCodec;

    /**
     * target socket ip address
     */
    protected HostAndPort targetAddress;

    protected IdSession session;

    public SocketIoDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }

    public void setMessageDispatcher(SocketIoDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
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

    @Override
    public IdSession getSession() {
        return session;
    }

}
