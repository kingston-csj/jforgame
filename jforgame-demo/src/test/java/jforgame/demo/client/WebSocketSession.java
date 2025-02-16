package jforgame.demo.client;

import jforgame.codec.MessageCodec;
import jforgame.demo.Player;
import jforgame.demo.game.login.message.req.ReqAccountLogin;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.MessageHeader;
import jforgame.socket.support.DefaultMessageHeader;

import javax.websocket.Session;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: caochaojie
 * @Date: 2025-02-16 22:10
 */
public class WebSocketSession implements IdSession {


    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private final long id;
    private final Session session;
    private final MessageCodec messageCodec;

    public WebSocketSession(Session session, MessageCodec messageCodec) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.session = session;
        this.messageCodec = messageCodec;
    }

    @Override
    public void sendAndClose(Object packet) throws IOException {
        try {
            send(packet);
        } finally {
            close();
        }
    }

    @Override
    public String getId() {
        return IdSession.super.getId();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        // WebSocket API 不直接提供远程地址，尝试从URI中获取
        try {
            String uri = session.getRequestURI().toString();
            String host = uri.split("://")[1].split("/")[0];
            String[] hostPort = host.split(":");
            String ip = hostPort[0];
            int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 80;
            return new InetSocketAddress(ip, port);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public String getRemoteIP() {
        InetSocketAddress address = getRemoteAddress();
        return address != null ? address.getHostString() : null;
    }

    @Override
    public int getRemotePort() {
        InetSocketAddress address = getRemoteAddress();
        return address != null ? address.getPort() : 0;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(getLocalIP(), getLocalPort());
    }

    @Override
    public String getLocalIP() {
        return "127.0.0.1"; // 默认返回本地地址
    }

    @Override
    public int getLocalPort() {
        return session.getContainer().getDefaultMaxSessionIdleTimeout() > 0 ?
                (int) session.getContainer().getDefaultMaxSessionIdleTimeout() : 0;
    }

    @Override
    public void setAttribute(String key, Object value) {
        session.getUserProperties().put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return session.getUserProperties().get(key);
    }

    @Override
    public Object getRawSession() {
        return session;
    }

    public void send(Object message) {
        try {
            MessageHeader header = new DefaultMessageHeader();
            Player.ReqAccountLogin reqAccountLogin = (Player.ReqAccountLogin) message;
            // 2. 编码消息体
            //byte[] messageBody = messageCodec.encode(message);
            byte[] messageBody = reqAccountLogin.toByteArray();
            // 3. 计算总长度并设置消息头
            int totalLength = messageBody.length + DefaultMessageHeader.SIZE;
            header.setMsgLength(totalLength);
            header.setCmd(GameMessageFactory.getInstance().getMessageId(ReqAccountLogin.class));
            header.setIndex(1);

            // 4. 获取消息头字节数组
            byte[] headerBytes = header.write();

            // 5. 创建一个能容纳消息头和消息体的ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(totalLength);

            // 6. 写入消息头和消息体
            byteBuffer.put(headerBytes);
            byteBuffer.put(messageBody);

            // 7. 准备读取
            byteBuffer.flip();

            // 8. 发送完整的消息
            session.getBasicRemote().sendBinary(byteBuffer);
            Thread.sleep(500000);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send message", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

 /*   @Override
    public void send(Object message) {
        try {
            MessageHeader header = new DefaultMessageHeader();

            // 2. 编码消息体
            byte[] messageBody = messageCodec.encode(message);

            // 3. 计算总长度并设置消息头
            int totalLength = messageBody.length + DefaultMessageHeader.SIZE;
            header.setMsgLength(totalLength);
            header.setCmd(GameMessageFactory.getInstance().getMessageId(message.getClass()));
            header.setIndex(1);

            // 4. 获取消息头字节数组
            byte[] headerBytes = header.write();

            // 5. 创建一个能容纳消息头和消息体的ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(totalLength);

            // 6. 写入消息头和消息体
            byteBuffer.put(headerBytes);
            byteBuffer.put(messageBody);

            // 7. 准备读取
            byteBuffer.flip();

            // 8. 发送完整的消息
            session.getBasicRemote().sendBinary(byteBuffer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }*/

    @Override
    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close session", e);
        }
    }


}
