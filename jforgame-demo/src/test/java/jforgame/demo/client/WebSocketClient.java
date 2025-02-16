package jforgame.demo.client;

import io.netty.buffer.ByteBuf;
import jforgame.codec.MessageCodec;
import jforgame.demo.game.login.message.res.ResAccountLogin;
import jforgame.socket.mina.support.client.TcpSocketClient;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.MessageHeader;
import jforgame.socket.support.DefaultMessageHeader;

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

                    // 添加二进制消息处理器
                    session.addMessageHandler((MessageHandler.Whole<java.nio.ByteBuffer>) data -> {
                        try {
                            byte[] bytes = new byte[data.remaining()];
                            data.get(bytes);
                            // 1. 读取消息头
                            MessageHeader header = new DefaultMessageHeader();
                            header.readServerBytes(bytes);
                            if (header.getCode() != 0) {
                                System.out.println("接收到服务器返回数据代码" + header.getCode());
                                return;
                            }
                            /**
                             * 读取消息体
                             */
                            int bodyLength = header.getMsgLength() - DefaultMessageHeader.SIZE - 4;
                            byte[] bodyData = new byte[bodyLength];
                            System.arraycopy(bytes, DefaultMessageHeader.SIZE + 4, bodyData, 0, bodyLength);
                            // 根据消息ID获取解析的类
                            Class<?> clazz = messageFactory.getMessage(header.getCmd());
                            Object decode = messageCodec.decode(ResAccountLogin.class, bodyData);
                            System.out.println("接收到服务器返回数据" + decode);
                            // 4. 创建RequestDataFrame并分发消息
//                                RequestDataFrame frame = new RequestDataFrame();
//                                frame.setMessage(message);
                            //   messageDispatcher.dispatch(session, frame);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //   messageDispatcher.exceptionCaught(session, e);
                        }
                    });
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
