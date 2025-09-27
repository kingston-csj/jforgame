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

/**
 * tcp服务器构建器
 */
public class TcpSocketServerBuilder {

    public static TcpSocketServerBuilder newBuilder() {
        return new TcpSocketServerBuilder();
    }

    List<HostAndPort> ipPortNodes = new ArrayList<>();

    /**
     * 最大协议字节数（包头+包体）
     */
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
     * 若某连接在指定的时间段内没有数据传输，则关闭该连接，单位为毫秒。
     * 默认为0，代表不启用
     */
    private int idleTime;

    /**
     * Leave it to support the addition of extended handler in the channel
     */
    private ExtendedChannelHandler extChannelHandler;

    boolean useEpollForLinux = false;

    boolean usePooledBuff = false;


    /**
     * 设置消息分发器
     *
     * @param socketIoDispatcher 消息分发器
     * @return this
     */
    public TcpSocketServerBuilder setSocketIoDispatcher(ChainedMessageDispatcher socketIoDispatcher) {
        this.socketIoDispatcher = socketIoDispatcher;
        return this;
    }

    /**
     * 设置消息工厂
     *
     * @param messageFactory 消息工厂
     * @return this
     */
    public TcpSocketServerBuilder setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    /**
     * 设置消息编码器
     *
     * @param messageCodec 消息编码器
     * @return this
     */
    public TcpSocketServerBuilder setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }

    /**
     * 设置绑定端口
     *
     * @param node 端口节点
     * @return this
     */
    public TcpSocketServerBuilder bindingPort(HostAndPort node) {
        this.ipPortNodes.add(node);
        return this;
    }

    /**
     * 设置最大协议字节数
     *
     * @param maxProtocolBytes 最大协议字节数
     * @return this
     */
    public TcpSocketServerBuilder setMaxProtocolBytes(int maxProtocolBytes) {
        this.maxProtocolBytes = maxProtocolBytes;
        return this;
    }

    /**
     * 设置空闲时间，单位秒
     *
     * @param idleTime 空闲时间
     * @return this
     */
    public TcpSocketServerBuilder setIdleTime(int idleTime) {
        this.idleTime = idleTime;
        return this;
    }

    /**
     * 设置是否使用epoll
     *
     * @param useEpollForLinux 是否使用epoll
     * @return this
     */
    public TcpSocketServerBuilder setUseEpollForLinux(boolean useEpollForLinux) {
        this.useEpollForLinux = useEpollForLinux;
        return this;
    }

    /**
     * 设置是否使用池化缓冲区
     *
     * @param usePooledBuff 是否使用池化缓冲区
     * @return this
     */
    public TcpSocketServerBuilder setUsePooledBuff(boolean usePooledBuff) {
        this.usePooledBuff = usePooledBuff;
        return this;
    }

    /**
     * 设置扩展通道处理器
     *
     * @param extChannelHandler 扩展通道处理器
     * @return this
     */
    public TcpSocketServerBuilder setExtChannelHandler(ExtendedChannelHandler extChannelHandler) {
        this.extChannelHandler = extChannelHandler;
        return this;
    }

    /**
     * 设置自定义私有协议栈解码器
     *
     * @param protocolDecoder 自定义私有协议栈解码器
     * @return this
     */
    public TcpSocketServerBuilder setProtocolDecoder(ByteToMessageDecoder protocolDecoder) {
        this.protocolDecoder = protocolDecoder;
        return this;
    }

    /**
     * 设置自定义私有协议栈编码器
     *
     * @param protocolEncoder 自定义私有协议栈编码器
     * @return this
     */
    public TcpSocketServerBuilder setProtocolEncoder(MessageToByteEncoder<Object> protocolEncoder) {
        this.protocolEncoder = protocolEncoder;
        return this;
    }

    /**
     * 构建服务端
     *
     * @return TcpSocketServer tcp服务器
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
