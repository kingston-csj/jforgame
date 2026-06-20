package jforgame.socket.core.protocol.message;

/**
 * Parsed private protocol stack package structure
 */
public class RequestDataFrame {

    /**
     * Message header
     */
    private MessageHeader header;

    /**
     * Message body, the specific content of the message
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
