package jforgame.socket.netty.support.server;

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
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
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
     * In the server side, the connection will be closed if it is idle for a certain period of time.
     */
    int idleMilliSeconds;

    SslContext sslContext;

    @Override
    public void start() throws Exception {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new WebSocketChannelInitializer());

            logger.info("socket server is listening at " + nodeConfig.getPort() + "......");
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
            pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(512 * 1024));
            pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler(websocketPath));

            // WebSocketFrame vs Message codec
            pipeline.addLast("socketFrameToMessage", new WebSocketFrameToSocketDataCodec(messageCodec, messageFactory));

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
