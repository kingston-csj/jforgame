package jforgame.socket.core.protocol.message;

/**
 * Socket data frame, used to identify a complete message package from client
 */
public class SocketDataFrame {

    /**
     * Client sequence number. If processing a client request package, return the message with client's sequence number.
     * If the message is actively sent by server, this field is 0.
     */
    private int index;

    /**
     * Specific message package
     */
    private Object message;

    public static SocketDataFrame withIndex(int index, Object message) {
        SocketDataFrame frame = new SocketDataFrame();
        frame.setMessage(message);
        frame.setIndex(index);
        return frame;
    }

    public static SocketDataFrame withoutIndex(Object message) {
        SocketDataFrame frame = new SocketDataFrame();
        frame.setMessage(message);
        return frame;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
