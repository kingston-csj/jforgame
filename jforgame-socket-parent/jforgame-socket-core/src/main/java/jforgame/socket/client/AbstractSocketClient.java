package jforgame.socket.client;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.SocketIoDispatcherAdapter;
import jforgame.socket.share.message.MessageFactory;

/**
 * 抽象的socket客户端
 * 提供更多的连接细节，参数配置
 */
public abstract class AbstractSocketClient implements SocketClient {

    /**
     * 啥事都不做的io dispatcher，当客户端不需要捕捉SocketIoDispatcher的各种IO事件旰，可使用该对象
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
