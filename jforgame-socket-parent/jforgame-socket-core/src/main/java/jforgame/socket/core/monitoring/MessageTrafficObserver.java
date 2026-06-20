package jforgame.socket.core.monitoring;

/**
 * Protocol traffic observer.
 * Used to count message sent/received times and bytes during protocol encoding/decoding.
 * @since 4.0.0
 */
public interface MessageTrafficObserver {

    /**
     * Inbound message observation
     *
     * @param cmd message id
     * @param bytes total protocol bytes
     */
    default void onInbound(int cmd, int bytes) {
    }

    /**
     * Outbound message observation
     *
     * @param cmd message id
     * @param bytes total protocol bytes
     */
    default void onOutbound(int cmd, int bytes) {
    }
}
