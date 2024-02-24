package jforgame.demo.socket.client;

import jforgame.codec.MessageCodec;
import jforgame.codec.struct.StructMessageCodec;
import jforgame.socket.client.CallbackTimeoutException;
import jforgame.socket.client.RpcMessageClient;
import jforgame.socket.client.SocketClient;
import jforgame.socket.client.Traceable;
import jforgame.socket.mina.client.MSocketClient;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.SocketIoDispatcherAdapter;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.support.DefaultMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RpcClientHelper {

    private static SocketIoDispatcher ioDispatcher = new SocketIoDispatcherAdapter() {
        @Override
        public void exceptionCaught(IdSession session, Throwable cause) {
            cause.printStackTrace();
        }
    };

    private static MessageFactory messageFactory = DefaultMessageFactory.getInstance();

    private static MessageCodec messageCodec = new StructMessageCodec();

    private static Logger logger = LoggerFactory.getLogger(RpcClientHelper.class.getName());

    public static void setIoDispatcher(SocketIoDispatcher ioDispatcher) {
        RpcClientHelper.ioDispatcher = ioDispatcher;
    }

    public static void setMessageFactory(MessageFactory messageFactory) {
        RpcClientHelper.messageFactory = messageFactory;
    }

    public static void setMessageCodec(MessageCodec messageCodec) {
        RpcClientHelper.messageCodec = messageCodec;
    }

    public Object onceRequest(String ip, int port, Traceable request) throws IOException, CallbackTimeoutException {
        SocketClient socketClient = new MSocketClient(ioDispatcher, messageFactory, messageCodec, HostAndPort.valueOf(ip, port));
        IdSession session = socketClient.openSession();
        try {
            Object response = RpcMessageClient.request(session, request);
            return response;
        } catch (CallbackTimeoutException e1) {
            throw e1;
        } finally {
            if (socketClient != null && session != null) {
                try {
                    socketClient.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

}
