package jforgame.socket.mina;

import jforgame.codec.MessageCodec;
import jforgame.socket.core.monitoring.DefaultTrafficObserver;
import jforgame.socket.core.monitoring.MessageTrafficObserver;
import jforgame.socket.core.protocol.message.MessageFactory;
import jforgame.socket.core.protocol.message.RequestDataFrame;
import jforgame.socket.core.protocol.message.DefaultMessageHeader;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protocol stack encoder.
 * This class provides a default private protocol stack encoder.
 * A complete data frame consists of two parts: message head and message body:
 * The message head contains the length of the data frame and message ID metadata, message sequence number (managed by client).
 * The message body only contains the message byte stream to be encoded. The specific encoding needs to be implemented
 * through the {@link MessageCodec#encode(Object)} method of the {@link MessageCodec} interface.
 */
public class DefaultProtocolDecoder extends CumulativeProtocolDecoder {

    private final Logger logger = LoggerFactory.getLogger("socketserver");
    /**
     * Maximum protocol data length
     */
    private final int maxProtocolBytes;
    /**
     * Message factory
     */
    private MessageFactory messageFactory;
    /**
     * Message decoder
     */
    private MessageCodec messageCodec;

    private final MessageTrafficObserver trafficObserver = DefaultTrafficObserver.INSTANCE;

    public DefaultProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this(messageFactory, messageCodec, 4096);
    }

    public DefaultProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec, int maxProtocolBytes) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.maxProtocolBytes = maxProtocolBytes;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (in.remaining() < DefaultMessageHeader.SIZE) {
            return false;
        }
        in.mark();

        // ----------------protocol pattern-------------------------
        //      header(12bytes)     | body
        // msgLength = 12+len(body) | body
        // msgLength | index | cmd  | body
        byte[] header = new byte[DefaultMessageHeader.SIZE];
        in.get(header);
        DefaultMessageHeader headerMeta = new DefaultMessageHeader();
        headerMeta.read(header);

        int length = headerMeta.getMsgLength();
        if (length > maxProtocolBytes) {
            logger.error("message data frame [{}] too large, close session now", length);
            session.close(true);
            return true;
        }

        int cmd = headerMeta.getCmd();
        int bodySize = length - DefaultMessageHeader.SIZE;
        if (in.remaining() < bodySize) {
            in.reset();
            return false;
        }

        byte[] body = new byte[bodySize];
        in.get(body);

        trafficObserver.onInbound(cmd, length);

        Class<?> msgClazz = messageFactory.getMessage(cmd);
        Object msg = messageCodec.decode(msgClazz, body);

        out.write(new RequestDataFrame(headerMeta, msg));
        return true;
    }

}
