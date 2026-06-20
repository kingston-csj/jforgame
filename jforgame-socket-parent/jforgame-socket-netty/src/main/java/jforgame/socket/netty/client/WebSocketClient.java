package jforgame.socket.netty.client;

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
import jforgame.socket.core.client.AbstractSocketClient;
import jforgame.socket.netty.NSession;
import jforgame.socket.netty.ChannelIoHandler;
import jforgame.socket.netty.server.WebSocketFrameToSocketDataCodec;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.SocketIoDispatcher;
import jforgame.socket.core.protocol.message.MessageFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket client
 */
public class WebSocketClient extends AbstractSocketClient {

    private final EventLoopGroup group = new NioEventLoopGroup(1);
    private final String wsPath;
    private SslContext sslContext;
    private boolean useSsl = false;
    //  Used for synchronization lock waiting for WebSocket handshake completion
    private CountDownLatch handshakeLatch;
    //  Record whether handshake was successful
    private boolean handshakeSuccess;
    //  Record handshake failure cause
    private Throwable handshakeFailureCause;

    public WebSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory,
                           MessageCodec messageCodec, HostAndPort hostPort, String wsPath) {
        this.ioDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.targetAddress = hostPort;
        this.wsPath = wsPath;
    }

    public WebSocketClient(MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort, String wsPath) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.targetAddress = hostPort;
        this.wsPath = wsPath;
    }

    @Override
    public IdSession openSession() throws IOException {
        // Initialize synchronization lock and state variables
        handshakeLatch = new CountDownLatch(1);
        handshakeSuccess = false;
        handshakeFailureCause = null;

        try {
            String scheme = useSsl ? "wss" : "ws";
            String host = targetAddress.getHost();
            int port = targetAddress.getPort();
            String path = wsPath == null || wsPath.isEmpty() ? "/" : wsPath;
            // Ensure path starts with slash
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            // Create WebSocket URI
            URI websocketUri = new URI(scheme, null, host, port, path, null, null);
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // SSL handler (if needed, put at the front)
                            if (sslContext != null) {
                                pipeline.addLast(sslContext.newHandler(ch.alloc()));
                            }

                            // HTTP basic handler (WebSocket is based on HTTP handshake)
                            pipeline.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(512 * 1024)
                            );

                            // WebSocket protocol handler (core, responsible for handshake and frame processing)
                            WebSocketClientProtocolHandler wsHandler = new WebSocketClientProtocolHandler(
                                    WebSocketClientHandshakerFactory.newHandshaker(
                                            websocketUri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()
                                    ),
                                    true,  // Auto handle close frame
                                    false, // Do not discard Pong frame
                                    5000   // Handshake timeout in milliseconds
                            );
                            pipeline.addLast(wsHandler);

                            // Add HandshakeCompletionListener to pipeline
                            // Must be placed after WebSocketClientProtocolHandler to receive its HandshakeComplete event
                            pipeline.addLast(new HandshakeCompletionListener());

                            // Business handler (codec, message dispatcher, etc., put at the end)
                            pipeline.addLast(new WebSocketFrameToSocketDataCodec(messageCodec, messageFactory));
                            pipeline.addLast((new CallbackHandler()));
                            pipeline.addLast(new ChannelIoHandler(ioDispatcher));
                        }
                    });
            ChannelFuture connectFuture = b.connect(host, port).sync();
            Channel channel = connectFuture.channel();
            // Subsequent connection, wait for handshake and other logic (omitted)
            // Wait for handshake result (up to 6 seconds to avoid infinite blocking)
            boolean isHandshakeDone = handshakeLatch.await(6, TimeUnit.SECONDS);
            if (!isHandshakeDone) {
                // Extreme case: listener did not receive any event (e.g., network interruption), proactively close channel
                channel.close().sync();
                throw new IOException("WebSocket handshake wait timeout");
            }
            // Check handshake result
            if (!handshakeSuccess) {
                // Handshake failed (timeout or exception), close channel and throw exception
                channel.close().sync();
                throw new IOException("WebSocket handshake failed", handshakeFailureCause);
            }

            // Handshake successful, return available session
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
            // Close EventLoopGroup (wait for all tasks to complete)
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Failed to close WebSocket session", e);
        }
    }


    private class HandshakeCompletionListener extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            // Listen for Netty native ClientHandshakeStateEvent events
            if (evt instanceof WebSocketClientProtocolHandler.ClientHandshakeStateEvent) {
                WebSocketClientProtocolHandler.ClientHandshakeStateEvent handshakeEvent = (WebSocketClientProtocolHandler.ClientHandshakeStateEvent) evt;
                switch (handshakeEvent) {
                    case HANDSHAKE_ISSUED:
                        break;
                    case HANDSHAKE_COMPLETE:
                        // Handshake successful (mark status, release lock)
                        handshakeSuccess = true;
                        handshakeFailureCause = null;
                        handshakeLatch.countDown(); // Release main thread blocking
                        break;

                    case HANDSHAKE_TIMEOUT:
                        // Handshake timeout (mark failure, release lock)
                        handshakeSuccess = false;
                        handshakeFailureCause = new IOException(
                                "WebSocket handshake timeout");
                        handshakeLatch.countDown();
                        break;
                }
            }
            // Continue passing other events (do not intercept Netty's other native events)
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // Handle other exceptions during handshake (e.g., SSL handshake failure, server rejecting connection)
            if (!handshakeSuccess && handshakeLatch.getCount() > 0) {
                handshakeSuccess = false;
                handshakeFailureCause = cause;
                handshakeLatch.countDown(); // Avoid main thread infinite blocking
            }
            super.exceptionCaught(ctx, cause);
        }
    }

}