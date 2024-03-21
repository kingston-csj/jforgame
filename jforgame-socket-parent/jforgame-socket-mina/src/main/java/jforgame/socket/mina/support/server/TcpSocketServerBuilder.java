package jforgame.socket.mina.support.server;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;

public class TcpSocketServerBuilder {

    public static TcpSocketServerBuilder newBuilder() {
        return new TcpSocketServerBuilder();
    }

    List<HostAndPort> ipPortNodes = new ArrayList<>();

    int maxProtocolSize = 4096;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    ChainedMessageDispatcher socketIoDispatcher;

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

        return socketServer;
    }
    
    
}
