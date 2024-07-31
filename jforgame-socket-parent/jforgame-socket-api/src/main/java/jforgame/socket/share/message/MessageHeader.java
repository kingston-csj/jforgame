package jforgame.socket.share.message;

import java.nio.ByteBuffer;

public class MessageHeader {

    /**
     * meta head size.
     */
    public static final int SIZE = 12;


    /**
     * 消息总长度，包括包头的12个字节，以及包体的长度
     */
    private int msgLength;

    /**
     * 消息包序，由客户端维护
     * 可用于客户端回调设计；或者服务器检测消息重播
     * 由客户端自行保证自增长
     */
    private int index;

    /**
     * 消息类型
     */
    private int cmd;

    public byte[] write() {
        ByteBuffer allocate = ByteBuffer.allocate(SIZE);
        allocate.putInt(msgLength);
        allocate.putInt(index);
        allocate.putInt(cmd);
        byte[] ret = allocate.array();
        allocate.clear();
        return ret;
    }

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

    public int getMsgLength() {
        return msgLength;
    }

    public int getIndex() {
        return index;
    }

    public int getCmd() {
        return cmd;
    }
}
