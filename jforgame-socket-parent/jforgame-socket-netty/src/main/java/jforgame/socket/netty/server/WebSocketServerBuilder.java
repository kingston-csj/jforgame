package jforgame.socket.netty.server;

import jforgame.codec.MessageCodec;
import jforgame.socket.netty.ChannelIoHandler;
import jforgame.socket.core.dispatch.ChainedMessageDispatcher;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.protocol.message.MessageFactory;

import java.io.File;

/**
 * WebSocket server builder
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

    //    private SslContext sslContext;
//    private boolean enableSsl = false; // SSL not enabled by default
//    private boolean useSelfSignedCert = true; // Whether to use self-signed certificate
    private String certDomain; // Certificate domain
    private File certChainFile; // Certificate chain file
    private File privateKeyFile; // Private key file
    private String keyPassword; // Private key password

    /**
     * WebSocket frame data type, default is text format.
     * 0 - TEXT
     * 1 - BINARY
     */
    private int frameType = WebSocketServer.FRAME_TYPE_TEXT;

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
     * Set websocket frame data type
     *
     * @param frameType frame data type, 0 - TEXT, 1 - BINARY
     * @return this
     */
    public WebSocketServerBuilder setFrameType(int frameType) {
        if (frameType != WebSocketServer.FRAME_TYPE_TEXT && frameType != WebSocketServer.FRAME_TYPE_BINARY) {
            throw new IllegalArgumentException("frameType must be 0 or 1");
        }
        this.frameType = frameType;
        return this;
    }


//    /**
//     * Enable self-signed certificate
//     *
//     * @param domain certificate domain
//     * @return
//     */
//    public WebSocketServerBuilder useSelfSignedCertificate(String domain) {
//        this.enableSsl = true; // Auto enable SSL
//        this.useSelfSignedCert = true;
//        this.certDomain = domain;
//        return this;
//    }
//
//    /**
//     * Enable formal certificate
//     *
//     * @param certChainFile  certificate file
//     * @param privateKeyFile private key file
//     */
//    public WebSocketServerBuilder useFormalCertificate(File certChainFile, File privateKeyFile) {
//        return useFormalCertificate(certChainFile, privateKeyFile, null);
//    }
//
//    /**
//     * Enable formal certificate
//     *
//     * @param certChainFile  certificate file
//     * @param privateKeyFile private key file
//     * @param keyPassword    private key password, pass null if none
//     */
//    public WebSocketServerBuilder useFormalCertificate(File certChainFile, File privateKeyFile, String keyPassword) {
//        this.enableSsl = true; // Auto enable SSL
//        this.useSelfSignedCert = false;
//        this.certChainFile = certChainFile;
//        this.privateKeyFile = privateKeyFile;
//        this.keyPassword = keyPassword;
//        return this;
//    }
//
//    /**
//     * Directly set SSL context
//     * Reserved interface for advanced users to manually set SSL context
//     *
//     * @param sslContext
//     */
//    public WebSocketServerBuilder setSslContext(SslContext sslContext) {
//        this.enableSsl = true; // Auto enable SSL
//        this.sslContext = sslContext;
//        return this;
//    }

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
//        if (enableSsl) {
//            if (sslContext == null) {
//                try {
//                    if (useSelfSignedCert) {
//                        // Use self-signed certificate
//                        SelfSignedCertificate ssc = certDomain != null ?
//                                new SelfSignedCertificate(certDomain) :
//                                new SelfSignedCertificate();
//                        sslContext = SslContextBuilder
//                                .forServer(ssc.certificate(), ssc.privateKey())
//                                .build();
//                    } else {
//                        // Use formal certificate
//                        if (certChainFile == null || privateKeyFile == null) {
//                            throw new IllegalArgumentException("certChainFile and privateKeyFile must not null when using formal certificate");
//                        }
//                        SslContextBuilder builder = SslContextBuilder.forServer(certChainFile, privateKeyFile);
//                        if (keyPassword != null) {
//                            builder.keyManager(certChainFile, privateKeyFile, keyPassword);
//                        }
//                        sslContext = builder.build();
//                    }
//                } catch (CertificateException | SSLException e) {
//                    throw new RuntimeException("Failed to initialize SSL context", e);
//                }
//            }
//        }

        // Create and configure server instance
        WebSocketServer socketServer = new WebSocketServer();
//        socketServer.sslContext = sslContext;
        socketServer.nodeConfig = hostPort;
        socketServer.maxProtocolBytes = maxProtocolBytes;
        socketServer.messageCodec = messageCodec;
        socketServer.messageFactory = messageFactory;
        socketServer.messageIoHandler = new ChannelIoHandler(socketIoDispatcher);
        socketServer.socketIoDispatcher = socketIoDispatcher;
        socketServer.websocketPath = websocketPath;
        socketServer.frameType = frameType;
        socketServer.idleMilliSeconds = idleMilliSeconds;

        return socketServer;
    }
}