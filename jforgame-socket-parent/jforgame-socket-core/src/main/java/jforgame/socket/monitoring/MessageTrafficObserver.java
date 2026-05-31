package jforgame.socket.monitoring;

/**
 * 协议流量观测器。
 * 用于在协议编解码阶段统计消息的收发次数和字节数。
 * @since 4.0.0
 */
public interface MessageTrafficObserver {

    /**
     * 入站消息观测
     *
     * @param cmd 消息id
     * @param bytes 协议总字节数
     */
    default void onInbound(int cmd, int bytes) {
    }

    /**
     * 出站消息观测
     *
     * @param cmd 消息id
     * @param bytes 协议总字节数
     */
    default void onOutbound(int cmd, int bytes) {
    }
}
