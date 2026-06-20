package jforgame.socket.core.protocol.message;

import java.nio.ByteBuffer;

/**
 * Default message header
 */
public class DefaultMessageHeader implements MessageHeader {

    /**
     * meta head size.
     */
    public static final int SIZE = 12;


    /**
     * Total message length, including 12 bytes of header and body length
     */
    private int msgLength;

    /**
     * Message sequence number, maintained by client.
     * Can be used for client callback design; or server message replay detection.
     * Client guarantees self-incrementing.
     */
    private int index;

    /**
     * Message type
     */
    private int cmd;

    @Override
    public byte[] write() {
        ByteBuffer allocate = ByteBuffer.allocate(SIZE);
        allocate.putInt(msgLength);
        allocate.putInt(index);
        allocate.putInt(cmd);
        byte[] ret = allocate.array();
        allocate.clear();
        return ret;
    }

    @Override
    public void read(byte[] bytes) {
        if (bytes == null || bytes.length != SIZE) {
            throw new IllegalArgumentException("invalid byte array, size must be " + SIZE);
        }

        ByteBuffer allocate = ByteBuffer.wrap(bytes);
        msgLength = allocate.getInt();
        index = allocate.getInt();
        cmd = allocate.getInt();

        allocate.clear();
    }

    @Override
    public int getMsgLength() {
        return msgLength;
    }

    @Override
    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getCmd() {
        return cmd;
    }

    @Override
    public void setCmd(int cmd) {
        this.cmd = cmd;
    }
}
