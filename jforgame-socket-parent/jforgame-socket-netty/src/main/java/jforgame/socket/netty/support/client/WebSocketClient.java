package jforgame.socket.netty.support.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebSocketClient extends AbstractSocketClient {

    private final EventLoopGroup group = new NioEventLoopGroup(1);
    private final String wsPath;
    private SslContext sslContext;
    private boolean useSsl = false;
    //  用于等待WebSocket握手完成的同步锁
    private CountDownLatch handshakeLatch;
    //  记录握手是否成功
    private boolean handshakeSuccess;
    //  记录握手失败原因
    private Throwable handshakeFailureCause;

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
        // 初始化同步锁和状态变量（省略，同之前的代码）
        handshakeLatch = new CountDownLatch(1);
        handshakeSuccess = false;
        handshakeFailureCause = null;

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

                            // 1. SSL 处理器（如需，放在最前面）
                            if (sslContext != null) {
                                pipeline.addLast(sslContext.newHandler(ch.alloc()));
                            }

                            // 2. HTTP 基础处理器（WebSocket 基于 HTTP 握手）
                            pipeline.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(512 * 1024)
                            );

                            // 3. WebSocket 协议处理器（核心，负责握手和帧处理）
                            WebSocketClientProtocolHandler wsHandler = new WebSocketClientProtocolHandler(
                                    WebSocketClientHandshakerFactory.newHandshaker(
                                            websocketUri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()
                                    ),
                                    true,  // 自动处理关闭帧
                                    false, // 不丢弃 Pong 帧
                                    5000   // 握手超时时间（毫秒）
                            );
                            pipeline.addLast(wsHandler);

                            // 4. 关键：添加 HandshakeCompletionListener 到 pipeline 中
                            // 必须放在 WebSocketClientProtocolHandler 之后，才能收到它发出的 HandshakeComplete 事件
                            pipeline.addLast(new HandshakeCompletionListener());

                            // 5. 业务处理器（编解码器、消息分发器等，放在最后）
                            pipeline.addLast(new WebSocketFrameToSocketDataCodec(messageCodec, messageFactory));
                            pipeline.addLast((new CallbackHandler()));
                            pipeline.addLast(new ChannelIoHandler(ioDispatcher));
                        }
                    });
            ChannelFuture connectFuture = b.connect(host, port).sync();
            Channel channel = connectFuture.channel();
            // 后续连接、等待握手等逻辑（省略）
            // 等待握手结果（最多等待 6 秒，避免无限阻塞）
            boolean isHandshakeDone = handshakeLatch.await(6, TimeUnit.SECONDS);
            if (!isHandshakeDone) {
                // 极端情况：监听器未收到任何事件（如网络中断），主动关闭通道
                channel.close().sync();
                throw new IOException("WebSocket握手等待超时");
            }
            // 检查握手结果
            if (!handshakeSuccess) {
                // 握手失败（超时或异常），关闭通道并抛出异常
                channel.close().sync();
                throw new IOException("WebSocket握手失败", handshakeFailureCause);
            }

            // 握手成功，返回可用的 session
            IdSession session = new NSession(channel);
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
            // 关闭EventLoopGroup（等待所有任务完成）
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Failed to close WebSocket session", e);
        }
    }


    private class HandshakeCompletionListener extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            // 监听 Netty 原生的 ClientHandshakeStateEvent 枚举事件
            if (evt instanceof WebSocketClientProtocolHandler.ClientHandshakeStateEvent) {
                WebSocketClientProtocolHandler.ClientHandshakeStateEvent handshakeEvent = (WebSocketClientProtocolHandler.ClientHandshakeStateEvent) evt;
                switch (handshakeEvent) {
                    case HANDSHAKE_ISSUED:
                        break;
                    case HANDSHAKE_COMPLETE:
                        // 握手成功（标记状态，释放锁）
                        handshakeSuccess = true;
                        handshakeFailureCause = null;
                        handshakeLatch.countDown(); // 释放主线程的阻塞
                        break;

                    case HANDSHAKE_TIMEOUT:
                        // 握手超时（标记失败，释放锁）
                        handshakeSuccess = false;
                        handshakeFailureCause = new IOException(
                                "WebSocket握手超时");
                        handshakeLatch.countDown();
                        break;
                }
            }
            // 继续传递其他事件（不拦截 Netty 其他原生事件）
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // 处理握手过程中的其他异常（如 SSL 握手失败、服务器拒绝连接）
            if (!handshakeSuccess && handshakeLatch.getCount() > 0) {
                handshakeSuccess = false;
                handshakeFailureCause = cause;
                handshakeLatch.countDown(); // 避免主线程无限阻塞
            }
            super.exceptionCaught(ctx, cause);
        }
    }

}