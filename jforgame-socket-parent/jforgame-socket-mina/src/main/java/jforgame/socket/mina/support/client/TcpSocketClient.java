package jforgame.socket.mina.support.client;

import jforgame.codec.MessageCodec;
import jforgame.socket.client.AbstractSocketClient;
import jforgame.socket.mina.MSession;
import jforgame.socket.mina.support.DefaultProtocolCodecFactory;
import jforgame.socket.mina.support.DefaultSocketIoHandler;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;
/**
 * TCP客户端
 * 此类封装了Mina的NioSocketConnector对象，提供了TCP客户端的连接、发送消息、关闭连接等操作。
 */
public class TcpSocketClient extends AbstractSocketClient {

    private final AttributeKey USER_SESSION = new AttributeKey(DefaultSocketIoHandler.class, "GameSession");

    public TcpSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort) {
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
                    new ProtocolCodecFilter(new DefaultProtocolCodecFactory(messageFactory, messageCodec)));
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
