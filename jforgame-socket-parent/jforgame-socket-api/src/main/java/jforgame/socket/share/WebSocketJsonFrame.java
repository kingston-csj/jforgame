package jforgame.socket.share;

/**
 * 当使用WebSocket传输文本格式的消息时，定义一个消息帧类
 * 客户端与服务器通信时，使用该类对应的JSON格式的文本消息
 */
public class WebSocketJsonFrame {

    /**
     * 包头：消息帧序号
     */
    public int index;
    /**
     * 包头：消息id
     */
    public int cmd;
    /**
     * 包体：消息对应的json字符串
     */
    public String msg;
}
