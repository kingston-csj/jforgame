package jforgame.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.core.monitoring.DefaultTrafficObserver;
import jforgame.socket.core.monitoring.MessageTrafficObserver;
import jforgame.socket.core.protocol.message.MessageFactory;
import jforgame.socket.core.protocol.message.SocketDataFrame;
import jforgame.socket.core.protocol.message.DefaultMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protocol stack encoder.
 * This class provides a default private protocol stack encoder.
 * A complete data frame consists of two parts: message head and message body:
 * The message head contains the length of the data frame and message ID metadata, message sequence number (managed by client).
 * The message body only contains the message byte stream to be encoded. The specific encoding needs to be implemented
 * through the {@link MessageCodec#encode(Object)} method of the {@link MessageCodec} interface.
 * Note: This class is annotated with {@link io.netty.channel.ChannelHandler.Sharable} annotation, so it can be shared
 * across different channel pipelines.
 * If using shared objects, make sure the {@link #messageCodec} instance is thread-safe.
 */
@ChannelHandler.Sharable
public class DefaultProtocolEncoder extends MessageToByteEncoder<Object> {

    private static final Logger logger = LoggerFactory.getLogger("socketserver");

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;

    private final MessageTrafficObserver trafficObserver = DefaultTrafficObserver.INSTANCE;

    public DefaultProtocolEncoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        assert message instanceof SocketDataFrame;
        SocketDataFrame dataFrame = (SocketDataFrame) message;
        // ----------------protocol pattern-------------------------
        //      header(12bytes)     | body
        // msgLength = 12+len(body) | body
        // msgLength | index | cmd  | body
        int cmd = messageFactory.getMessageId(dataFrame.getMessage().getClass());
        try {
            byte[] body = messageCodec.encode(dataFrame.getMessage());
            // Write header
            // message content length
            int msgLength = body.length + DefaultMessageHeader.SIZE;
            out.writeInt(msgLength);
            out.writeInt(dataFrame.getIndex());
            // Write cmd type
            out.writeInt(cmd);

            // Write body
            out.writeBytes(body);

            trafficObserver.onOutbound(cmd, msgLength);
        } catch (Exception e) {
            logger.error("wrote message {} failed", cmd, e);
        }
    }

}
