package jforgame.socket.client;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;

public abstract class AbstractSocketClient implements SocketClient {

    protected SocketIoDispatcher ioDispatcher;

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
