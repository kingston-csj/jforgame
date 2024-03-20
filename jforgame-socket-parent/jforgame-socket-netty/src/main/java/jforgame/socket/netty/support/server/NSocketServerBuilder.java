package jforgame.socket.netty.support.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jforgame.codec.MessageCodec;
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.netty.support.DefaultProtocolDecoder;
import jforgame.socket.netty.support.DefaultProtocolEncoder;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NSocketServerBuilder {

    public static NSocketServerBuilder newBuilder() {
        return new NSocketServerBuilder();
    }

    List<HostAndPort> ipPortNodes = new ArrayList<>();

    int maxProtocolBytes = 4096;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    ChainedMessageDispatcher socketIoDispatcher;

    private ChannelIoHandler channelIoHandler;

    private final ServerIdleHandler serverIdleHandler = new ServerIdleHandler();

    /**
     * In the server side, the connection will be closed if it is idle for a certain period of time.
     */
    private int idleTime;

    /**
     * Leave it to support the addition of extended handler in the channel
     */
    private ExtendedChannelHandler extChannelHandler;

    boolean useEpollForLinux = false;

    boolean usePooledBuff = false;


    public NSocketServerBuilder setSocketIoDispatcher(ChainedMessageDispatcher socketIoDispatcher) {
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

    public NSocketServerBuilder setIdleTime(int idleTime) {
        this.idleTime = idleTime;
        return this;
    }


    public NSocketServerBuilder setUseEpollForLinux(boolean useEpollForLinux) {
        this.useEpollForLinux = useEpollForLinux;
        return this;
    }

    public NSocketServerBuilder setUsePooledBuff(boolean usePooledBuff) {
        this.usePooledBuff = usePooledBuff;
        return this;
    }

    public NSocketServerBuilder setExtChannelHandler(ExtendedChannelHandler extChannelHandler) {
        this.extChannelHandler = extChannelHandler;
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

        socketServer.usePooledBuff = usePooledBuff;
        socketServer.useEpollForLinux = useEpollForLinux;

        socketServer.childChannelInitializer = new ChildChannelHandler();

        return socketServer;
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            ChannelPipeline pipeline = arg0.pipeline();
            if (extChannelHandler != null) {
                List<ChannelHandler> frontDoor = extChannelHandler.frontChannelHandlers();
                if (frontDoor != null) {
                    for (ChannelHandler channelHandler : frontDoor) {
                        pipeline.addLast(channelHandler.getClass().getName(), channelHandler);
                    }
                }
            }
            pipeline.addLast("protocolDecoder", new DefaultProtocolDecoder(messageFactory, messageCodec, maxProtocolBytes));
            pipeline.addLast("protocolEncoder", new DefaultProtocolEncoder(messageFactory, messageCodec));
            if (idleTime > 0) {
                // 客户端XXX没收发包，便会触发UserEventTriggered事件到IdleEventHandler
                pipeline.addLast(new IdleStateHandler(0, 0, idleTime,
                        TimeUnit.MILLISECONDS));
                pipeline.addLast("serverIdleHandler", serverIdleHandler);
            }
            pipeline.addLast("socketIoHandler", channelIoHandler);
            if (extChannelHandler != null) {
                List<ChannelHandler> backdoor = extChannelHandler.backChannelHandlers();
                if (backdoor != null) {
                    for (ChannelHandler channelHandler : backdoor) {
                        pipeline.addLast(channelHandler.getClass().getName(), channelHandler);
                    }
                }
            }
        }
    }
}
