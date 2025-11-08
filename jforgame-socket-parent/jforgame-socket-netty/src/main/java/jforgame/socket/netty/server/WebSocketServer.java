package jforgame.socket.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jforgame.codec.MessageCodec;
import jforgame.socket.netty.ChannelIoHandler;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.server.ServerNode;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * simple web socket server
 * support {@link TextWebSocketFrame} and  {@link BinaryWebSocketFrame}
 */
public class WebSocketServer implements ServerNode {

    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    SocketIoDispatcher socketIoDispatcher;

    ChannelIoHandler messageIoHandler;

    // 避免使用默认线程数参数
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

    HostAndPort nodeConfig;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    String websocketPath;


    /**
     * 最大协议字节数（二进制为包头+包体, 文本为json字符串长度）
     */
    int maxProtocolBytes = 512 * 1024;


    /**
     * In the server side, the connection will be closed if it is idle for a certain period of time.
     */
    int idleMilliSeconds;

    SslContext sslContext;

    /**
     * websocket帧数据类型--文本帧
     */
    public static int FRAME_TYPE_TEXT = 0;
    /**
     * websocket帧数据类型--二进制帧
     */
    public static int FRAME_TYPE_BINARY = 1;

    /**
     * websocket帧数据类型
     */
    int frameType;

    @Override
    public void start() throws Exception {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new WebSocketChannelInitializer());

            logger.info("socket server is listening at {}......", nodeConfig.getPort());
            serverBootstrap.bind(new InetSocketAddress(nodeConfig.getPort())).sync();
        } catch (Exception e) {
            logger.error("", e);

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            throw e;
        }
    }

    @Override
    public void shutdown() throws Exception {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            if (sslContext != null) {
                pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));
            }
            pipeline.addLast("httpServerCodec", new HttpServerCodec());
            pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
            pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(64 * 1024));
            pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler(websocketPath, null, false, maxProtocolBytes));
            pipeline.addLast("webSocketFrameAggregator", new WebSocketFrameAggregator(maxProtocolBytes));
            // WebSocketFrame vs Message codec
            pipeline.addLast("socketFrameToMessage", new WebSocketFrameToSocketDataCodec(frameType, messageCodec, messageFactory));

            if (idleMilliSeconds > 0) {
                // 客户端XXX没收发包，便会触发UserEventTriggered事件到IdleEventHandler
                pipeline.addLast(new IdleStateHandler(0, 0, idleMilliSeconds,
                        TimeUnit.MILLISECONDS));
                pipeline.addLast("serverIdleHandler", new ServerIdleHandler(socketIoDispatcher));
            }

            pipeline.addLast(messageIoHandler);
        }
    }


}
