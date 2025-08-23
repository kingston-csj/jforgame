package jforgame.socket.share.message;

/**
 * 解析过的私有协议栈包结构
 */
public class RequestDataFrame {

    /**
     * 消息包头
     */
    private MessageHeader header;

    /**
     * 消息包体，即消息的具体内容
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
