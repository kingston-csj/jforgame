package jforgame.socket.mina.client;

import jforgame.codec.MessageCodec;
import jforgame.socket.client.AbstractSocketClient;
import jforgame.socket.client.CallBackService;
import jforgame.socket.client.RpcResponseData;
import jforgame.socket.client.Traceable;
import jforgame.socket.mina.MSession;
import jforgame.socket.mina.support.DefaultProtocolCodecFactory;
import jforgame.socket.mina.support.DefaultSocketIoHandler;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MSocketClient extends AbstractSocketClient {

    private AttributeKey USER_SESSION = new AttributeKey(DefaultSocketIoHandler.class, "GameSession");

    public MSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort) {
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
            connector.setHandler(new IoHandlerAdapter() {
                @Override
                public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                    IdSession userSession = (IdSession) session.getAttribute(USER_SESSION);
                    ioDispatcher.exceptionCaught(userSession, cause);
                }
                @Override
                public void messageReceived(IoSession session, Object data) throws Exception {
                    IdSession userSession = (IdSession) session.getAttribute(USER_SESSION);
                    if (data instanceof Traceable) {
                        Traceable traceable = (Traceable) data;
                        RpcResponseData responseData = new RpcResponseData();
                        responseData.setResponse(data);
                        CallBackService.getInstance().fillCallBack(traceable.getIndex(), responseData);
                    }
                    //交由消息分发器处理
                    ioDispatcher.dispatch(userSession, data);
                }
            });

            ConnectFuture future = connector.connect(new InetSocketAddress(targetAddress.getHost(), targetAddress.getPort()));
            future.awaitUninterruptibly();
            IoSession ioSession = future.getSession();
            IdSession userSession = new MSession(ioSession);
            ioSession.setAttribute(USER_SESSION,
                    userSession);
            this.session = userSession;
            return userSession;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.session.close();
    }
}
