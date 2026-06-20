package jforgame.socket.core.net;

/**
 * When using WebSocket to transmit text format messages, define a message frame class.
 * When client and server communicate, use JSON format text messages corresponding to this class.
 */
public class WebSocketJsonFrame {

    /**
     * Header: message frame sequence number
     */
    public int index;
    /**
     * Header: message id
     */
    public int cmd;
    /**
     * Body: json string corresponding to the message
     */
    public String msg;
}
