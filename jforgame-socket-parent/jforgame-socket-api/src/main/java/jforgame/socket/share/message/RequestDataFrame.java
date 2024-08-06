package jforgame.socket.share.message;

/**
 * 解析过的私有协议栈包结构
 */
public class RequestDataFrame {

    /**
     * message header meta
     */
    private MessageHeader header;

    /**
     * message bean rather than bytes data
     */
    private Object message;

    public RequestDataFrame(MessageHeader header, Object message) {
        this.header = header;
        this.message = message;
    }

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
