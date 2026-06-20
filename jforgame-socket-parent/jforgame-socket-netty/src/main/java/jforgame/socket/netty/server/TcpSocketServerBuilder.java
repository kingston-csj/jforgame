package jforgame.socket.netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import jforgame.codec.MessageCodec;
import jforgame.socket.netty.ChannelIoHandler;
import jforgame.socket.netty.DefaultProtocolDecoder;
import jforgame.socket.netty.DefaultProtocolEncoder;
import jforgame.socket.core.dispatch.ChainedMessageDispatcher;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.protocol.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TCP server builder
 */
public class TcpSocketServerBuilder {

    public static TcpSocketServerBuilder newBuilder() {
        return new TcpSocketServerBuilder();
    }

    List<HostAndPort> ipPortNodes = new ArrayList<>();

    /**
     * Maximum protocol bytes (header + body)
     */
    int maxProtocolBytes = 4096;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    ChainedMessageDispatcher socketIoDispatcher;

    /**
     * Custom private protocol stack decoder
     */
    ByteToMessageDecoder protocolDecoder;

    /**
     * Custom private protocol stack encoder
     */
    MessageToByteEncoder<Object> protocolEncoder;

    private ChannelIoHandler channelIoHandler;

    private ServerIdleHandler serverIdleHandler;

    /**
     * If a connection has no data transmission within the specified time period, the connection will be closed. Unit is milliseconds.
     * Default is 0, meaning not enabled
     */
    private int idleTime;

    /**
     * Leave it to support the addition of extended handler in the channel
     */
    private ExtendedChannelHandler extChannelHandler;

    boolean useEpollForLinux = false;

    boolean usePooledBuff = false;


    /**
     * Set message dispatcher
     *
     * @param socketIoDispatcher message dispatcher
     * @return this
     */
    public TcpSocketServerBuilder setSocketIoDispatcher(ChainedMessageDispatcher socketIoDispatcher) {
        this.socketIoDispatcher = socketIoDispatcher;
        return this;
    }

    /**
     * Set message factory
     *
     * @param messageFactory message factory
     * @return this
     */
    public TcpSocketServerBuilder setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    /**
     * Set message codec
     *
     * @param messageCodec message codec
     * @return this
     */
    public TcpSocketServerBuilder setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }

    /**
     * Set bind port
     *
     * @param node port node
     * @return this
     */
    public TcpSocketServerBuilder bindingPort(HostAndPort node) {
        this.ipPortNodes.add(node);
        return this;
    }

    /**
     * Set max protocol bytes
     *
     * @param maxProtocolBytes max protocol bytes
     * @return this
     */
    public TcpSocketServerBuilder setMaxProtocolBytes(int maxProtocolBytes) {
        this.maxProtocolBytes = maxProtocolBytes;
        return this;
    }

    /**
     * Set idle time in milliseconds
     *
     * @param idleTime idle time
     * @return this
     */
    public TcpSocketServerBuilder setIdleTime(int idleTime) {
        this.idleTime = idleTime;
        return this;
    }

    /**
     * Set whether to use epoll
     *
     * @param useEpollForLinux whether to use epoll
     * @return this
     */
    public TcpSocketServerBuilder setUseEpollForLinux(boolean useEpollForLinux) {
        this.useEpollForLinux = useEpollForLinux;
        return this;
    }

    /**
     * Set whether to use pooled buffer
     *
     * @param usePooledBuff whether to use pooled buffer
     * @return this
     */
    public TcpSocketServerBuilder setUsePooledBuff(boolean usePooledBuff) {
        this.usePooledBuff = usePooledBuff;
        return this;
    }

    /**
     * Set extended channel handler
     *
     * @param extChannelHandler extended channel handler
     * @return this
     */
    public TcpSocketServerBuilder setExtChannelHandler(ExtendedChannelHandler extChannelHandler) {
        this.extChannelHandler = extChannelHandler;
        return this;
    }

    /**
     * Set custom private protocol stack decoder
     *
     * @param protocolDecoder custom private protocol stack decoder
     * @return this
     */
    public TcpSocketServerBuilder setProtocolDecoder(ByteToMessageDecoder protocolDecoder) {
        this.protocolDecoder = protocolDecoder;
        return this;
    }

    /**
     * Set custom private protocol stack encoder
     *
     * @param protocolEncoder custom private protocol stack encoder
     * @return this
     */
    public TcpSocketServerBuilder setProtocolEncoder(MessageToByteEncoder<Object> protocolEncoder) {
        this.protocolEncoder = protocolEncoder;
        return this;
    }

    /**
     * Build server
     *
     * @return TcpSocketServer tcp server
     */
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
            if (protocolDecoder != null) {
                pipeline.addLast("protocolDecoder", protocolDecoder);
            } else {
                pipeline.addLast("protocolDecoder", new DefaultProtocolDecoder(messageFactory, messageCodec, maxProtocolBytes));
            }
            if (protocolEncoder != null) {
                pipeline.addLast("protocolEncoder", protocolEncoder);
            } else {
                pipeline.addLast("protocolEncoder", new DefaultProtocolEncoder(messageFactory, messageCodec));
            }

            if (idleTime > 0) {
                // If client XXX does not send/receive packets, UserEventTriggered event will be triggered to IdleEventHandler
                pipeline.addLast(new IdleStateHandler(0, 0, idleTime,
                        TimeUnit.MILLISECONDS));
                pipeline.addLast("serverIdleHandler", new ServerIdleHandler(socketIoDispatcher));
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
