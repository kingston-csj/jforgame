package jforgame.socket.share.message;

/**
 * 解析过的私有协议栈包结构
 */
public class RequestDataFrame {

    /**
     * 包头元信息
     */
    private MessageHeader header;

    /**
     * 包体具体消息
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
