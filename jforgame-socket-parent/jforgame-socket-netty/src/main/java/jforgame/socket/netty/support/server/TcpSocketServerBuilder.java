package jforgame.socket.netty.support.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
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

public class TcpSocketServerBuilder {

    public static TcpSocketServerBuilder newBuilder() {
        return new TcpSocketServerBuilder();
    }

    List<HostAndPort> ipPortNodes = new ArrayList<>();

    int maxProtocolBytes = 4096;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    ChainedMessageDispatcher socketIoDispatcher;

    /**
     * 自定义私有协议栈解码器
     */
    ByteToMessageDecoder protocolDecoder;

    /**
     * 自定义私有协议栈编码器
     */
    MessageToByteEncoder<Object> protocolEncoder;

    private ChannelIoHandler channelIoHandler;

    private ServerIdleHandler serverIdleHandler;

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

    public TcpSocketServerBuilder setMaxProtocolBytes(int maxProtocolBytes) {
        this.maxProtocolBytes = maxProtocolBytes;
        return this;
    }

    public TcpSocketServerBuilder setIdleTime(int idleTime) {
        this.idleTime = idleTime;
        return this;
    }


    public TcpSocketServerBuilder setUseEpollForLinux(boolean useEpollForLinux) {
        this.useEpollForLinux = useEpollForLinux;
        return this;
    }

    public TcpSocketServerBuilder setUsePooledBuff(boolean usePooledBuff) {
        this.usePooledBuff = usePooledBuff;
        return this;
    }

    public TcpSocketServerBuilder setExtChannelHandler(ExtendedChannelHandler extChannelHandler) {
        this.extChannelHandler = extChannelHandler;
        return this;
    }

    public TcpSocketServerBuilder setProtocolDecoder(ByteToMessageDecoder protocolDecoder) {
        this.protocolDecoder = protocolDecoder;
        return this;
    }

    public TcpSocketServerBuilder setProtocolEncoder(MessageToByteEncoder<Object> protocolEncoder) {
        this.protocolEncoder = protocolEncoder;
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
                // 客户端XXX没收发包，便会触发UserEventTriggered事件到IdleEventHandler
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
