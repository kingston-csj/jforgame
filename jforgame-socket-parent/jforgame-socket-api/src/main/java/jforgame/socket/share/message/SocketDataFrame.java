package jforgame.socket.share.message;

public class SocketDataFrame {

    /**
     * 客户端序号，若处理的是客户端请求包，返回消息时带上客户端的序号
     * 若由服务器主动下发的消息，该字段为0
     */
    private int index;

    /**
     * 具体消息包
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
