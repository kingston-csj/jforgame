package jforgame.socket.share;

import jforgame.socket.client.Traceable;

import java.net.InetSocketAddress;

/**
 * 点对点session，不与任何nio框架绑定
 *
 * @author kinson
 */
public interface IdSession {

    String ID = "ID";

    void send(Object packet);

    /**
     * send message with index.
     * @param index
     * @param packet
     */
    default void send(int index, Object packet) {
        if (!(packet instanceof Traceable)) {
            throw new IllegalArgumentException(packet.getClass().getName() + " must be Traceable");
        }
        Traceable traceable = (Traceable) packet;
        traceable.setIndex(index);
        send(packet);
    }

    long getOwnerId();

    InetSocketAddress getRemoteAddress();

    String getRemoteIP();

    int getRemotePort();

    InetSocketAddress getLocalAddress();

    String getLocalIP();

    int getLocalPort();

    /**
     * update session attribute
     *
     * @param key
     * @param value
     * @return
     */
    Object setAttribute(String key, Object value);

    /**
     * get session attribute
     *
     * @param key
     * @return
     */
    Object getAttribute(String key);

    Object getRawSession();

}
