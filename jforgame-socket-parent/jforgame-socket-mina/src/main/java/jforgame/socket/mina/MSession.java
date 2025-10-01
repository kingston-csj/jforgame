package jforgame.socket.mina;

import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.SocketDataFrame;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于mina的会话实现，提供了会话相关的操作方法。
 * 会话是指客户端与服务器之间的一次通信，会话中包含了客户端与服务器的连接信息、会话属性等。
 * 会话的生命周期从客户端连接到服务器开始，到客户端断开连接或服务器主动关闭会话结束。
 * 会话还可以包含一些扩展属性，用于存储会话相关的信息。
 */
public class MSession implements IdSession {

    private static Logger logger = LoggerFactory.getLogger(IdSession.class);

    protected IoSession session;

    /**
     * 拓展属性
     */
    protected Map<String, Object> attrs = new HashMap<>();

    public MSession(IoSession session) {
        this.session = session;
    }

    @Override
    public void send(Object packet) {
        if (packet instanceof SocketDataFrame) {
            session.write(packet);
        } else {
            session.write(SocketDataFrame.withoutIndex(packet));
        }
    }

    @Override
    public void sendAndClose(Object packet) {
        IoFutureListener<IoFuture> closeListener = ioFuture -> {
            if (ioFuture.isDone()) {
                try {
                    close();
                } catch (IOException e) {
                    logger.info("", e);
                }
            }
        };
        if (packet instanceof SocketDataFrame) {
            session.write(packet).addListener(closeListener);
        } else {
            session.write(SocketDataFrame.withoutIndex(packet)).addListener(closeListener);
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        if (session == null) {
            return null;
        }
        return ((InetSocketAddress) session.getRemoteAddress());
    }

    @Override
    public String getRemoteIP() {
        if (session == null) {
            return "";
        }
        return ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public int getRemotePort() {
        if (session == null) {
            return -1;
        }
        return ((InetSocketAddress) session.getRemoteAddress()).getPort();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        if (session == null) {
            return null;
        }
        return ((InetSocketAddress) session.getLocalAddress());
    }

    @Override
    public String getLocalIP() {
        if (session == null) {
            return "";
        }
        return ((InetSocketAddress) session.getLocalAddress()).getAddress().getHostAddress();
    }

    @Override
    public int getLocalPort() {
        if (session == null) {
            return -1;
        }
        return ((InetSocketAddress) session.getLocalAddress()).getPort();
    }

    @Override
    public Object getAttribute(String key) {
        return attrs.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attrs.put(key, value);
    }

    @Override
    public IoSession getRawSession() {
        return session;
    }

    @Override
    public void close() throws IOException {
        this.session.close(true);
    }

}
