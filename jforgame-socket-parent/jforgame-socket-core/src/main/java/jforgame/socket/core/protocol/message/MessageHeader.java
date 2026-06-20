package jforgame.socket.core.protocol.message;

/**
 * Private protocol stack - message header definition.
 * Defined using interface, users can modify the type of specified fields (e.g., cmd) according to their needs.
 * For example: network io can define cmd as short type, while application uniformly receives as int.
 * This saves some network IO, and these objects are "short-lived small objects" with little impact on GC.
 */
public interface MessageHeader {


    byte[] write();

    void read(byte[] bytes);

    /**
     * Total length of private protocol stack, including header + body
     * @return total length of private protocol stack
     */
    int getMsgLength();

    void setMsgLength(int msgLength);

    /**
     * Message sequence number (guaranteed auto-incremented by client).
     * Can be used for client callbacks, message replay detection.
     * @return message sequence number
     */
    int getIndex();


    void setIndex(int index);


    /**
     * Message type
     * @return message type
     */
    int getCmd();


    void setCmd(int cmd);

}
