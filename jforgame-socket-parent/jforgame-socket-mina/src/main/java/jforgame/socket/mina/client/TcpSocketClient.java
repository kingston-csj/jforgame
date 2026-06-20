package jforgame.socket.mina.client;

import jforgame.codec.MessageCodec;
import jforgame.socket.core.client.AbstractSocketClient;
import jforgame.socket.mina.MSession;
import jforgame.socket.mina.DefaultProtocolCodecFactory;
import jforgame.socket.mina.DefaultSocketIoHandler;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.SocketIoDispatcher;
import jforgame.socket.core.protocol.message.MessageFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * TCP client.
 * This class encapsulates Mina's NioSocketConnector object, providing TCP client connection, message sending,
 * connection closing and other operations.
 */
public class TcpSocketClient extends AbstractSocketClient {

    private final AttributeKey USER_SESSION = new AttributeKey(DefaultSocketIoHandler.class, "GameSession");

    private final ProtocolCodecFactory protocolCodecFactory;

    public TcpSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort) {
        this.ioDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.targetAddress = hostPort;
        this.protocolCodecFactory = new DefaultProtocolCodecFactory(messageFactory, messageCodec);
    }

    public TcpSocketClient(ProtocolCodecFactory protocolCodecFactory, SocketIoDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort) {
        this.protocolCodecFactory = protocolCodecFactory;
        this.ioDispatcher = messageDispatcher;
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.targetAddress = hostPort;
    }

    @Override
    public IdSession openSession() throws IOException {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(protocolCodecFactory));
            connector.setHandler(new DefaultClientSocketIoHandler(ioDispatcher));

            ConnectFuture future = connector.connect(new InetSocketAddress(targetAddress.getHost(), targetAddress.getPort()));
            future.awaitUninterruptibly();
            IoSession ioSession = future.getSession();
            IdSession userSession = new MSession(ioSession);
            ioSession.setAttribute(USER_SESSION,
                    userSession);
            this.session = userSession;
            return userSession;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.session.close();
    }
}
