package jforgame.socket.mina;

import jforgame.codec.MessageCodec;
import jforgame.socket.core.monitoring.DefaultTrafficObserver;
import jforgame.socket.core.monitoring.MessageTrafficObserver;
import jforgame.socket.core.protocol.message.MessageFactory;
import jforgame.socket.core.protocol.message.SocketDataFrame;
import jforgame.socket.core.protocol.message.DefaultMessageHeader;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protocol stack decoder.
 * This class provides a default private protocol stack decoder.
 * A complete data frame consists of two parts: message head and message body:
 * The message head contains the length of the data frame and message ID metadata, message sequence number (managed by client).
 * The message body includes the byte message that needs to be decoded by {@link MessageCodec}.
 *
 * @see MessageCodec#decode(Class, byte[])
 */
public class DefaultProtocolEncoder implements ProtocolEncoder {

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;

    private final int WRITE_BUFF_SIZE = 1024;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageTrafficObserver trafficObserver = DefaultTrafficObserver.INSTANCE;

    public DefaultProtocolEncoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
    }

    @Override
    public void dispose(IoSession arg0) throws Exception {

    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        assert message instanceof SocketDataFrame;
        SocketDataFrame dataFrame = (SocketDataFrame) message;
        IoBuffer buffer = writeMessage(dataFrame);
        try {
            out.write(buffer);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private IoBuffer writeMessage(SocketDataFrame frame) throws Exception {
        // ----------------protocol pattern-------------------------
        //      header(12bytes)     | body
        // msgLength = 12+len(body) | body
        // msgLength | index | cmd  | body

        IoBuffer buffer = IoBuffer.allocate(WRITE_BUFF_SIZE);
        buffer.setAutoExpand(true);
        Object message = frame.getMessage();
        byte[] body = messageCodec.encode(message);
        // the length of message body
        int msgLength = body.length + DefaultMessageHeader.SIZE;
        int cmd = messageFactory.getMessageId(message.getClass());

        // Write header
        // message content length
        buffer.putInt(msgLength);
        buffer.putInt(frame.getIndex());
        // Write cmd type
        buffer.putInt(cmd);

        // Write body
        buffer.put(body);
        // Return to buffer byte array head
        buffer.flip();

        trafficObserver.onOutbound(cmd, msgLength);
        return buffer;
    }

}
