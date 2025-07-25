
package jforgame.socket.netty.support.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import jforgame.codec.MessageCodec;
import jforgame.socket.client.AbstractSocketClient;
import jforgame.socket.netty.NSession;
import jforgame.socket.netty.support.ChannelIoHandler;
import jforgame.socket.netty.support.server.WebSocketFrameToSocketDataCodec;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;

import java.io.IOException;
import java.net.URI;

/**
 * WebSocket客户端实现
 * @since 2.3.0
 */
public class WebSocketClient extends AbstractSocketClient {

    private final EventLoopGroup group = new NioEventLoopGroup(1);
    private final String wsPath;
    private SslContext sslContext;
    private boolean useSsl = false;

    public WebSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory,
                           MessageCodec messageCodec, HostAndPort hostPort, String wsPath) {
        this.ioDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.targetAddress = hostPort;
        this.wsPath = wsPath;
    }

    @Override
    public IdSession openSession() throws IOException {
        try {
            String scheme = useSsl ? "wss" : "ws";
            String host = targetAddress.getHost();
            int port = targetAddress.getPort();
            String path = wsPath == null || wsPath.isEmpty() ? "/" : wsPath;
            // 确保路径以斜杠开头
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            // 创建WebSocket URI
            URI websocketUri = new URI(scheme, null, host, port, path, null, null);

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 添加SSL处理器（如果需要）
                            if (sslContext != null) {
                                pipeline.addLast(sslContext.newHandler(ch.alloc()));
                            }

                            // 添加HTTP和WebSocket处理器
                            pipeline.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(512 * 1024),

                                    // WebSocket协议处理器（适配新构造函数）
                                    new WebSocketClientProtocolHandler(
                                            WebSocketClientHandshakerFactory.newHandshaker(
                                                    websocketUri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()
                                            ),
                                            true,                // 处理关闭帧
                                            false,               // 不丢弃Pong帧
                                            5000                 // 握手超时5秒
                                    )
                            );

                            // 添加协议编解码器和消息处理器
                            pipeline.addLast(new WebSocketFrameToSocketDataCodec(messageCodec, messageFactory));
                            pipeline.addLast((new CallbackHandler()));
                            pipeline.addLast(new ChannelIoHandler(ioDispatcher));
                        }
                    });

            ChannelFuture f = b.connect(host, port).sync();
            IdSession session = new NSession(f.channel());
            this.session = session;
            return session;
        } catch (Exception e) {
            group.shutdownGracefully();
            throw new IOException("Failed to open WebSocket session", e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (session != null) {
                session.close();
            }
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Failed to close WebSocket session", e);
        }
    }

}