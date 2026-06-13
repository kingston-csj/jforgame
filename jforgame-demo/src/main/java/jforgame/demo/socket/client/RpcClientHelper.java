package jforgame.demo.socket.client;

import jforgame.codec.MessageCodec;
import jforgame.codec.struct.StructCodec;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.socket.core.client.CallbackTimeoutException;
import jforgame.socket.core.client.RpcMessageClient;
import jforgame.socket.core.client.SocketClient;
import jforgame.socket.mina.client.TcpSocketClient;
import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.SocketIoDispatcher;
import jforgame.socket.core.dispatch.SocketIoDispatcherAdapter;
import jforgame.socket.core.protocol.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RpcClientHelper {

    private static SocketIoDispatcher ioDispatcher = new SocketIoDispatcherAdapter() {
        @Override
        public void exceptionCaught(IdSession session, Throwable cause) {
            logger.error("", cause);
        }
    };

    private static MessageFactory messageFactory = GameMessageFactory.getInstance();

    private static MessageCodec messageCodec = new StructCodec();

    private static final Logger logger = LoggerFactory.getLogger(RpcClientHelper.class.getName());

    public static void setIoDispatcher(SocketIoDispatcher ioDispatcher) {
        RpcClientHelper.ioDispatcher = ioDispatcher;
    }

    public static void setMessageFactory(MessageFactory messageFactory) {
        RpcClientHelper.messageFactory = messageFactory;
    }

    public static void setMessageCodec(MessageCodec messageCodec) {
        RpcClientHelper.messageCodec = messageCodec;
    }

    public Object onceRequest(String ip, int port, Object request) throws IOException, CallbackTimeoutException {
        SocketClient socketClient = new TcpSocketClient(ioDispatcher, messageFactory, messageCodec, HostAndPort.valueOf(ip, port));
        IdSession session = socketClient.openSession();
        try {
            return RpcMessageClient.request(session, request);
        } finally {
            if (session != null) {
                try {
                    socketClient.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

}
