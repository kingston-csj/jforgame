package jforgame.socket.mina;

import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.protocol.message.SocketDataFrame;
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
 * Session implementation based on MINA, provides session-related operations.
 * A session is a single communication between client and server, containing connection information and session attributes.
 * The session lifecycle starts when client connects to server and ends when client disconnects or server proactively closes the session.
 * Sessions can also contain some extended attributes for storing session-related information.
 */
public class MSession implements IdSession {

    private static Logger logger = LoggerFactory.getLogger(IdSession.class);

    protected IoSession session;

    /**
     * Extended attributes
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
