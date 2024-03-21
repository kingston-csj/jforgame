package jforgame.socket.share;

import jforgame.socket.client.Traceable;

import java.io.Closeable;
import java.net.InetSocketAddress;

/**
 * A socket session abstraction. Allows sending messages over a socket
 * connection and closing it.
 *
 * @author kinson
 */
public interface IdSession extends Closeable {

    String ID = "ID";

    /**
     * get a unique session identifier
     * @return Return a unique session identifier
     */
    default String getId() {
        if (getAttribute(ID) != null) {
            return getAttribute(ID).toString();
        }
        return "";
    }

    void send(Object packet);

    /**
     * send message with index.
     * when client wants to send a message and then gets its response, the message must be {@link  Traceable}
     * @param index index of the request message
     * @param packet message to send
     */
    default void send(int index, Object packet) {
        if (!(packet instanceof Traceable)) {
            throw new IllegalArgumentException(packet.getClass().getName() + " must be Traceable");
        }
        Traceable traceable = (Traceable) packet;
        traceable.setIndex(index);
        send(packet);
    }

    InetSocketAddress getRemoteAddress();

    String getRemoteIP();

    int getRemotePort();

    InetSocketAddress getLocalAddress();

    String getLocalIP();

    int getLocalPort();

    /**
     * update session attribute
     *
     * @param key  key of the attribute
     * @param value value of the attribute
     */
    void setAttribute(String key, Object value);

    /**
     * get session attribute
     *
     * @param key target attribute key
     * @return the value associated with the key
     */
    Object getAttribute(String key);

    Object getRawSession();

}
