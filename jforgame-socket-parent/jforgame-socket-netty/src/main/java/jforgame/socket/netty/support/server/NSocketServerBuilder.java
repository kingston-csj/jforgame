package jforgame.socket.netty.support.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jforgame.codec.MessageCodec;
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.netty.support.DefaultProtocolDecoder;
import jforgame.socket.netty.support.DefaultProtocolEncoder;
import jforgame.socket.share.BaseSocketIoDispatcher;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;

public class NSocketServerBuilder {

    public static NSocketServerBuilder builder() {
        return new NSocketServerBuilder();
    }

    List<HostAndPort> ipPortNodes = new ArrayList<>();

    int maxProtocolBytes = 4096;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    BaseSocketIoDispatcher socketIoDispatcher;

    ChannelInitializer<SocketChannel> childChannelInitializer;

    private ChannelIoHandler channelIoHandler;

    public NSocketServerBuilder setSocketIoDispatcher(BaseSocketIoDispatcher socketIoDispatcher) {
        this.socketIoDispatcher = socketIoDispatcher;
        return this;
    }

    public NSocketServerBuilder setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    public NSocketServerBuilder setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }

    public NSocketServerBuilder bindingPort(HostAndPort node) {
        this.ipPortNodes.add(node);
        return this;
    }

    public NSocketServerBuilder setMaxProtocolBytes(int maxProtocolBytes) {
        this.maxProtocolBytes = maxProtocolBytes;
        return this;
    }

    public NSocketServerBuilder setChildChannelInitializer(ChannelInitializer<SocketChannel> childChannelInitializer) {
        this.childChannelInitializer = childChannelInitializer;
        return this;
    }

    public NSocketServer build() {
        NSocketServer socketServer = new NSocketServer();
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
        channelIoHandler = new ChannelIoHandler(socketIoDispatcher);

        if (childChannelInitializer != null) {
            socketServer.childChannelInitializer = childChannelInitializer;
        } else {
            socketServer.childChannelInitializer = new ChildChannelHandler();
        }

        return socketServer;
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            ChannelPipeline pipeline = arg0.pipeline();
            pipeline.addLast(new DefaultProtocolDecoder(messageFactory, messageCodec, maxProtocolBytes));
            pipeline.addLast(new DefaultProtocolEncoder(messageFactory, messageCodec));
            // 客户端300秒没收发包，便会触发UserEventTriggered事件到IdleEventHandler
            pipeline.addLast(new IdleStateHandler(300, 300, 300));
            pipeline.addLast(channelIoHandler);
        }
    }
}
