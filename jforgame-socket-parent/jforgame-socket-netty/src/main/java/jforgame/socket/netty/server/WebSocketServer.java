package jforgame.socket.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
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
import jforgame.socket.core.server.ServerNode;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.dispatch.SocketIoDispatcher;
import jforgame.socket.core.protocol.message.MessageFactory;
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

    // Avoid using default thread count parameter
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

    HostAndPort nodeConfig;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    String websocketPath;


    /**
     * Max protocol bytes (binary is header+body, text is json string length)
     */
    int maxProtocolBytes = 512 * 1024;


    /**
     * In the server side, the connection will be closed if it is idle for a certain period of time.
     */
    int idleMilliSeconds;

    SslContext sslContext;

    /**
     * websocket frame data type -- text frame
     */
    public static int FRAME_TYPE_TEXT = 0;
    /**
     * websocket frame data type -- binary frame
     */
    public static int FRAME_TYPE_BINARY = 1;

    /**
     * websocket frame data type
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
            // Normalize ws url, filter parameters after ?
            pipeline.addLast("normalizationUrl", new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (msg instanceof FullHttpRequest) {
                        FullHttpRequest req = (FullHttpRequest) msg;
                        String uri = req.uri();
                        int idx = uri.indexOf('?');
                        if (idx > 0) {
                            req.setUri(uri.substring(0, idx));
                        }
                    }
                    super.channelRead(ctx, msg);
                }
            });

            pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler(websocketPath, null, true, maxProtocolBytes));
            pipeline.addLast("webSocketFrameAggregator", new WebSocketFrameAggregator(maxProtocolBytes));
            // WebSocketFrame vs Message codec
            pipeline.addLast("socketFrameToMessage", new WebSocketFrameToSocketDataCodec(frameType, messageCodec, messageFactory));

            if (idleMilliSeconds > 0) {
                // If client XXX does not send/receive packets, UserEventTriggered event will be triggered to IdleEventHandler
                pipeline.addLast(new IdleStateHandler(0, 0, idleMilliSeconds,
                        TimeUnit.MILLISECONDS));
                pipeline.addLast("serverIdleHandler", new ServerIdleHandler(socketIoDispatcher));
            }

            pipeline.addLast(messageIoHandler);
        }
    }


}
