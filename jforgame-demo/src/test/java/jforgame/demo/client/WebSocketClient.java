package jforgame.demo.client;

import jforgame.codec.MessageCodec;
import jforgame.socket.mina.support.client.TcpSocketClient;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: caochaojie
 * @Date: 2025-02-16 21:47
 */
public class WebSocketClient extends TcpSocketClient {

    public WebSocketClient(SocketIoDispatcher messageDispatcher, MessageFactory messageFactory, MessageCodec messageCodec, HostAndPort hostPort) {
        super(messageDispatcher, messageFactory, messageCodec, hostPort);
    }

    @Override
    public IdSession openSession() throws IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        CountDownLatch latch = new CountDownLatch(1);
        try {
            String host = getTargetAddress().getHost();
            Session session1 = container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    System.out.println("WebSocket连接成功！");
                    latch.countDown();
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.println("WebSocket连接关闭！");
                    latch.countDown();
                }

                @Override
                public void onError(Session session, Throwable thr) {
                    thr.printStackTrace();
                    latch.countDown();
                }

            }, URI.create(host));

            latch.await();
            IdSession session = new WebSocketSession(session1, messageCodec);
            return session;
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to WebSocket server", e);
        }
    }

    @Override
    public void close() throws IOException {
        session.close();
    }
}
