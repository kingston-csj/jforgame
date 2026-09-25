package jforgame.socket.netty.server;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import jforgame.codec.MessageCodec;
import jforgame.socket.core.dispatch.ChainedMessageDispatcher;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.protocol.message.MessageFactory;
import jforgame.socket.netty.ChannelIoHandler;
import jforgame.socket.netty.WebSocketFrameType;

import javax.net.ssl.SSLException;
import java.io.File;
import java.security.cert.CertificateException;

/**
 * WebSocket server builder
 * SSL usage:
 * 1. Test environment: use {@link #useSelfSignedCertificate(String)} to let Netty generate
 * a self-signed certificate at runtime.
 * 2. Production environment: use {@link #useFormalCertificate(File, File)} or
 * {@link #useFormalCertificate(File, File, String)} to load the formal certificate and private key.
 * 3. Advanced usage: use {@link #setSslContext(SslContext)} to inject a custom SSL context.
 */
public class WebSocketServerBuilder {

    /**
     * Create new builder
     *
     * @return builder
     */
    public static WebSocketServerBuilder newBuilder() {
        return new WebSocketServerBuilder();
    }

    private HostAndPort hostPort;
    private MessageFactory messageFactory;
    private MessageCodec messageCodec;
    private ChainedMessageDispatcher socketIoDispatcher;
    private String websocketPath = "/ws";

    /**
     * Maximum protocol bytes (header + body)
     */
    int maxProtocolBytes = 512 * 1024;

    private SslContext sslContext;
    private boolean enableSsl = false; // SSL not enabled by default
    private boolean useSelfSignedCert = true; // Whether to use self-signed certificate
    private String certDomain; // Certificate domain
    private File certChainFile; // Certificate chain file
    private File privateKeyFile; // Private key file
    private String keyPassword; // Private key password

    /**
     * WebSocket frame data type, default is auto-detect (server).
     * 0 - AUTO (server auto-detect per client)
     * 1 - TEXT (force text)
     * 2 - BINARY (force binary)
     */
    private int frameType = WebSocketFrameType.FRAME_TYPE_AUTO;

    /**
     * In the server side, the connection will be closed if it is idle for a certain period of time.
     * unit is MILLISECONDS
     */
    private int idleMilliSeconds;

    /**
     * Set message dispatcher
     *
     * @param socketIoDispatcher message dispatcher
     * @return this
     */
    public WebSocketServerBuilder setSocketIoDispatcher(ChainedMessageDispatcher socketIoDispatcher) {
        this.socketIoDispatcher = socketIoDispatcher;
        return this;
    }

    /**
     * Set message factory
     *
     * @param messageFactory message factory
     * @return this
     */
    public WebSocketServerBuilder setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    /**
     * Set message codec
     *
     * @param messageCodec message codec
     * @return this
     */
    public WebSocketServerBuilder setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }

    /**
     * Set websocket path
     *
     * @param websocketPath websocket path
     * @return this
     */
    public WebSocketServerBuilder setWebsocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
        return this;
    }

    /**
     * Bind port
     *
     * @param hostPort port
     * @return this
     */
    public WebSocketServerBuilder bindingPort(HostAndPort hostPort) {
        this.hostPort = hostPort;
        return this;
    }

    /**
     * Set connection idle time in milliseconds
     *
     * @param idleMilliSeconds connection idle time in milliseconds
     * @return this
     */
    public WebSocketServerBuilder setIdleMilliSeconds(int idleMilliSeconds) {
        this.idleMilliSeconds = idleMilliSeconds;
        return this;
    }

    /**
     * Set maximum protocol bytes (header + body)
     *
     * @param maxProtocolBytes maximum protocol bytes
     * @return this
     */
    public WebSocketServerBuilder setMaxProtocolBytes(int maxProtocolBytes) {
        this.maxProtocolBytes = maxProtocolBytes;
        return this;
    }

    /**
     * Set websocket outbound frame type
     *
     * @param frameType frame type rule:
     *                  {@link WebSocketFrameType#FRAME_TYPE_AUTO} (0) = adaptive mode (server),
     *                  each client connection independently determines its encoding format
     *                  based on the first uplink business frame;
     *                  {@link WebSocketFrameType#FRAME_TYPE_TEXT} (1) = force all outbound messages
     *                  to TextWebSocketFrame;
     *                  {@link WebSocketFrameType#FRAME_TYPE_BINARY} (2) = force all outbound messages
     *                  to BinaryWebSocketFrame
     * @return this
     */
    public WebSocketServerBuilder setFrameType(int frameType) {
        if (frameType != WebSocketFrameType.FRAME_TYPE_AUTO
                && frameType != WebSocketFrameType.FRAME_TYPE_TEXT
                && frameType != WebSocketFrameType.FRAME_TYPE_BINARY) {
            throw new IllegalArgumentException("frameType must be 0 (AUTO), 1 (TEXT) or 2 (BINARY)");
        }
        this.frameType = frameType;
        return this;
    }


    /**
     * Enable self-signed certificate, just for test
     * Typical usage: local development / joint debugging / automated tests.
     * Do not use this in production because the certificate is not trusted by browsers or standard clients.
     *
     * @param domain certificate domain
     * @return this
     */
    public WebSocketServerBuilder useSelfSignedCertificate(String domain) {
        this.enableSsl = true; // Auto enable SSL
        this.useSelfSignedCert = true;
        this.certDomain = domain;
        return this;
    }

    /**
     * Enable formal certificate
     * Typical usage: production or pre-release environment.
     * The certificate domain should match the domain used by the client when connecting with wss://.
     *
     * @param certChainFile  certificate file
     * @param privateKeyFile private key file
     * @return this
     */
    public WebSocketServerBuilder useFormalCertificate(File certChainFile, File privateKeyFile) {
        return useFormalCertificate(certChainFile, privateKeyFile, null);
    }

    /**
     * Enable formal certificate
     * Typical usage: production or pre-release environment.
     * The certificate domain should match the domain used by the client when connecting with wss://.
     *
     * @param certChainFile  certificate file
     * @param privateKeyFile private key file
     * @param keyPassword    private key password, pass null if none
     * @return this
     */
    public WebSocketServerBuilder useFormalCertificate(File certChainFile, File privateKeyFile, String keyPassword) {
        this.enableSsl = true; // Auto enable SSL
        this.useSelfSignedCert = false;
        this.certChainFile = certChainFile;
        this.privateKeyFile = privateKeyFile;
        this.keyPassword = keyPassword;
        return this;
    }

    /**
     * Directly set SSL context
     * Reserved interface for advanced users to manually set SSL context
     * Suitable when certificate loading or trust policy is managed outside this builder.
     *
     * @param sslContext ssl context
     * @return this
     */
    public WebSocketServerBuilder setSslContext(SslContext sslContext) {
        this.enableSsl = true; // Auto enable SSL
        this.sslContext = sslContext;
        return this;
    }

    public WebSocketServer build() {
        // Validate required parameters
        if (socketIoDispatcher == null) {
            throw new IllegalArgumentException("socketIoDispatcher must not null");
        }
        if (messageFactory == null) {
            throw new IllegalArgumentException("messageFactory must not null");
        }
        if (messageCodec == null) {
            throw new IllegalArgumentException("messageCodec must not null");
        }
        if (hostPort == null) {
            throw new IllegalArgumentException("hostPort must not null");
        }

        // Configure SSL context
        if (enableSsl && sslContext == null) {
            try {
                if (useSelfSignedCert) {
                    SelfSignedCertificate ssc = certDomain != null
                            ? new SelfSignedCertificate(certDomain)
                            : new SelfSignedCertificate();
                    sslContext = SslContextBuilder
                            .forServer(ssc.certificate(), ssc.privateKey())
                            .build();
                } else {
                    if (certChainFile == null || privateKeyFile == null) {
                        throw new IllegalArgumentException("certChainFile and privateKeyFile must not null when using formal certificate");
                    }
                    SslContextBuilder builder = SslContextBuilder.forServer(certChainFile, privateKeyFile);
                    if (keyPassword != null) {
                        builder.keyManager(certChainFile, privateKeyFile, keyPassword);
                    }
                    sslContext = builder.build();
                }
            } catch (CertificateException | SSLException e) {
                throw new RuntimeException("Failed to initialize SSL context", e);
            }
        }

        // Create and configure server instance
        WebSocketServer socketServer = new WebSocketServer();
        socketServer.sslContext = sslContext;
        socketServer.nodeConfig = hostPort;
        socketServer.maxProtocolBytes = maxProtocolBytes;
        socketServer.messageCodec = messageCodec;
        socketServer.messageFactory = messageFactory;
        socketServer.messageIoHandler = new ChannelIoHandler(socketIoDispatcher);
        socketServer.socketIoDispatcher = socketIoDispatcher;
        socketServer.websocketPath = websocketPath;
        socketServer.idleMilliSeconds = idleMilliSeconds;
        socketServer.frameType = frameType;

        return socketServer;
    }
}
