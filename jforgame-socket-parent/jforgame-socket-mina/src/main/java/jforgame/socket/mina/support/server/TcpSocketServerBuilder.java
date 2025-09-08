package jforgame.socket.mina.support.server;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.message.MessageFactory;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * TCP服务器构建器
 * 此类用于构建TCP服务器，提供了设置服务器参数、绑定端口、构建服务器等操作。
 */
public class TcpSocketServerBuilder {

    public static TcpSocketServerBuilder newBuilder() {
        return new TcpSocketServerBuilder();
    }

    List<HostAndPort> ipPortNodes = new ArrayList<>();

    int maxProtocolSize = 4096;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    ChainedMessageDispatcher socketIoDispatcher;

    ProtocolCodecFactory protocolCodecFactory;

    public TcpSocketServerBuilder setSocketIoDispatcher(ChainedMessageDispatcher socketIoDispatcher) {
        this.socketIoDispatcher = socketIoDispatcher;
        return this;
    }

    public TcpSocketServerBuilder setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    public TcpSocketServerBuilder setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }

    public TcpSocketServerBuilder bindingPort(HostAndPort node) {
        this.ipPortNodes.add(node);
        return this;
    }

    public TcpSocketServerBuilder setMaxProtocolSize(int maxProtocolSize) {
        this.maxProtocolSize = maxProtocolSize;
        return this;
    }

    public TcpSocketServerBuilder setProtocolCodecFactory(ProtocolCodecFactory protocolCodecFactory) {
        this.protocolCodecFactory = protocolCodecFactory;
        return this;
    }

    public TcpSocketServer build() {
        TcpSocketServer socketServer = new TcpSocketServer();
        if (socketIoDispatcher == null) {
            throw new IllegalArgumentException("socketIoDispatcher must not null");
        }
        if (messageFactory == null) {
            throw new IllegalArgumentException("messageFactory must not null");
        }
        if (messageCodec == null) {
            throw new IllegalArgumentException("messageCodec must not null");
        }
        if (ipPortNodes.isEmpty()) {
            throw new IllegalArgumentException("ipPortNodes must not null");
        }
        socketServer.nodesConfig = ipPortNodes;
        socketServer.messageFactory = messageFactory;
        socketServer.messageCodec = messageCodec;
        socketServer.socketIoDispatcher = socketIoDispatcher;
        socketServer.maxProtocolSize = maxProtocolSize;
        socketServer.protocolCodecFactory = protocolCodecFactory;

        return socketServer;
    }
    
    
}
