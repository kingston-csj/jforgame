package jforgame.socket.netty.support.server;

import jforgame.codec.MessageCodec;
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.message.MessageFactory;

public class WebSocketServerBuilder {

    public static WebSocketServerBuilder newBuilder() {
        return new WebSocketServerBuilder();
    }

    HostAndPort hortPort;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    ChainedMessageDispatcher socketIoDispatcher;

    private String websocketPath = "/ws";

//    private SslContext sslContext;
//
//    private boolean ssl = true;

    public WebSocketServerBuilder setSocketIoDispatcher(ChainedMessageDispatcher socketIoDispatcher) {
        this.socketIoDispatcher = socketIoDispatcher;
        return this;
    }

    public WebSocketServerBuilder setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    public WebSocketServerBuilder setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }


    public WebSocketServerBuilder setWebsocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
        return this;
    }

    public WebSocketServerBuilder bindingPort(HostAndPort node) {
        this.hortPort = node;
        return this;
    }

    public WebSocketServer build() {
        WebSocketServer socketServer = new WebSocketServer();
        if (socketIoDispatcher == null) {
            throw new IllegalArgumentException("socketIoDispatcher must not null");
        }
        if (messageFactory == null) {
            throw new IllegalArgumentException("messageFactory must not null");
        }
        if (messageCodec == null) {
            throw new IllegalArgumentException("messageCodec must not null");
        }
        if (hortPort == null) {
            throw new IllegalArgumentException("nodeConfig must not null");
        }

//        if (ssl) {
//            if (sslContext == null) {
//                try {
//                    SelfSignedCertificate ssc = new SelfSignedCertificate();
//                    sslContext = SslContextBuilder
//                            .forServer(ssc.certificate(), ssc.privateKey())
//                            .build();
//                } catch (CertificateException | SSLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            socketServer.sslContext = sslContext;
//        }

        socketServer.nodeConfig = hortPort;
        socketServer.messageCodec = messageCodec;
        socketServer.messageFactory = messageFactory;
        socketServer.messageIoHandler = new ChannelIoHandler(socketIoDispatcher);
        socketServer.websocketPath = websocketPath;

        return socketServer;
    }


}
