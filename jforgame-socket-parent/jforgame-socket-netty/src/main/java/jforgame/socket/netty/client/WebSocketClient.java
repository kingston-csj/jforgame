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
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import jforgame.codec.MessageCodec;
import jforgame.socket.core.client.AbstractSocketClient;
import jforgame.socket.core.dispatch.SocketIoDispatcher;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.protocol.message.MessageFactory;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.netty.ChannelIoHandler;
import jforgame.socket.netty.NSession;
import jforgame.socket.netty.WebSocketFrameType;
import jforgame.socket.netty.server.WebSocketFrameToSocketDataCodec;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket client
 * SSL usage:
 * 1. If wsPath starts with wss://, this client will automatically enable SSL.
 * 2. Test environment with self-signed server certificate:
 * use {@link #useInsecureSslForTest()} or provide a custom trust policy via {@link #setSslContext(SslContext)}.
 * 3. Production environment with formal certificate:
 * connect directly with wss://domain/path and do not use {@link #useInsecureSslForTest()}.
 */
public class WebSocketClient extends AbstractSocketClient {

    private final EventLoopGroup group = new NioEventLoopGroup(1);
    private final String wsPath;
    private SslContext sslContext;
    //  Used for synchronization lock waiting for WebSocket handshake completion
    private CountDownLatch handshakeLatch;
    //  Record whether handshake was successful
    private boolean handshakeSuccess;
    //  Record handshake failure cause
    private Throwable handshakeFailureCause;

    private int frameType;

    /**
     *
     * @param messageDispatcher
     * @param messageFactory
     * @param messageCodec
     * @param frameType         1 text frame 2 binary frame
     * @param hostPort
     * @param wsPath
     */
    public WebSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec, int frameType, HostAndPort hostPort, String wsPath) {
        this.ioDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.targetAddress = hostPort;
        this.frameType = frameType;
        this.wsPath = wsPath;
    }

    public WebSocketClient(MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort, String wsPath) {
        this(EMPTY_DISPATCHER, messageFactory, messageCodec, WebSocketFrameType.FRAME_TYPE_TEXT, hostPort, wsPath);
    }

    /**
     * Inject a custom client SSL context.
     * Typical usage:
     * 1. Production environment: provide a custom trust store or mutual TLS configuration.
     * 2. Test environment: provide a custom trust policy instead of trusting all certificates.
     */
    public WebSocketClient setSslContext(SslContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * Test-only helper.
     * Trusts all server certificates so the client can connect to a wss server using a self-signed certificate.
     * Do not use this in production.
     */
    public WebSocketClient useInsecureSslForTest() {
        try {
            this.sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            return this;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize test ssl context", e);
        }
    }

    @Override
    public IdSession openSession() throws IOException {
        // Initialize synchronization lock and state variables
        handshakeLatch = new CountDownLatch(1);
        handshakeSuccess = false;
        handshakeFailureCause = null;

        try {
            URI websocketUri;
            String host;
            int port;

            // If wsPath already carries ws:// or wss://, use it directly.
            // In particular, wss:// means SSL should be enabled for this connection.
            if (wsPath != null && (wsPath.startsWith("ws://") || wsPath.startsWith("wss://"))) {
                websocketUri = URI.create(wsPath);
                host = websocketUri.getHost();
                port = websocketUri.getPort();
                if (port < 0) {
                    port = "wss".equalsIgnoreCase(websocketUri.getScheme()) ? 443 : 80;
                }
            } else {
                String path = wsPath == null || wsPath.isEmpty() ? "/" : wsPath;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                host = targetAddress.getHost();
                port = targetAddress.getPort();
                websocketUri = new URI("ws", null, host, port, path, null, null);
            }

            // For formal certificates, the default client SSL context is usually enough.
            // For self-signed certificates in test environments, call useInsecureSslForTest()
            // or inject a custom sslContext before openSession().
            if ("wss".equalsIgnoreCase(websocketUri.getScheme()) && sslContext == null) {
                sslContext = SslContextBuilder.forClient().build();
            }

            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();

                    // SSL handler (if needed, put at the front)
                    if (sslContext != null) {
                        pipeline.addLast(sslContext.newHandler(ch.alloc()));
                    }

                    // HTTP basic handler (WebSocket is based on HTTP handshake)
                    pipeline.addLast(new HttpClientCodec(), new HttpObjectAggregator(512 * 1024));

                    // WebSocket protocol handler (core, responsible for handshake and frame processing)
                    WebSocketClientProtocolHandler wsHandler = new WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(websocketUri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()), true,  // Auto handle close frame
                            false, // Do not discard Pong frame
                            5000   // Handshake timeout in milliseconds
                    );
                    pipeline.addLast(wsHandler);

                    // Add HandshakeCompletionListener to pipeline
                    // Must be placed after WebSocketClientProtocolHandler to receive its HandshakeComplete event
                    pipeline.addLast(new HandshakeCompletionListener());

                    // Business handler (codec, message dispatcher, etc., put at the end)
                    pipeline.addLast(new WebSocketFrameToSocketDataCodec(frameType, messageCodec, messageFactory));
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
                        handshakeFailureCause = new IOException("WebSocket handshake timeout");
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
