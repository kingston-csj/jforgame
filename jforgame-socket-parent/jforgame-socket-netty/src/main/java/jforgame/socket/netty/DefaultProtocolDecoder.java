package jforgame.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.core.monitoring.DefaultTrafficObserver;
import jforgame.socket.core.monitoring.MessageTrafficObserver;
import jforgame.socket.core.protocol.message.MessageFactory;
import jforgame.socket.core.protocol.message.RequestDataFrame;
import jforgame.socket.core.protocol.message.DefaultMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Protocol stack decoder.
 * This class provides a default private protocol stack decoder.
 * A complete data frame consists of two parts: message head and message body:
 * The message head contains the length of the data frame and message ID metadata, message sequence number (managed by client).
 * The message body includes the byte message that needs to be decoded by {@link MessageCodec}.
 *
 * @see MessageCodec#decode(Class, byte[])
 */
public class DefaultProtocolDecoder extends ByteToMessageDecoder {

    /**
     * Max protocol bytes (header + body)
     */
    private int maxProtocolBytes;

    private final Logger logger = LoggerFactory.getLogger("socketserver");

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;

    private final MessageTrafficObserver trafficObserver = DefaultTrafficObserver.INSTANCE;


    public DefaultProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this(messageFactory, messageCodec, 4096);
    }

    public DefaultProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec, int maxProtocolBytes) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.maxProtocolBytes = maxProtocolBytes;
    }

    public void setMaxProtocolBytes(int maxProtocolBytes) {
        this.maxProtocolBytes = maxProtocolBytes;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < DefaultMessageHeader.SIZE) {
            return;
        }
        in.markReaderIndex();
        // ----------------protocol pattern-------------------------
        //      header(12bytes)     | body
        // msgLength = 12+len(body) | body
        // msgLength | index | cmd  | body
        byte[] header = new byte[DefaultMessageHeader.SIZE];
        in.readBytes(header);
        DefaultMessageHeader headerMeta = new DefaultMessageHeader();
        headerMeta.read(header);

        int length = headerMeta.getMsgLength();
        if (length > maxProtocolBytes) {
            logger.error("message data frame [{}] too large, close session now", length);
            ctx.close();
            return;
        }
        int bodySize = length - DefaultMessageHeader.SIZE;
        if (in.readableBytes() < bodySize) {
            in.resetReaderIndex();
            return;
        }
        int cmd = headerMeta.getCmd();
        byte[] body = new byte[bodySize];
        in.readBytes(body);

        trafficObserver.onInbound(cmd, length);

        Class<?> msgClazz = messageFactory.getMessage(cmd);

        Object message = messageCodec.decode(msgClazz, body);
        out.add(new RequestDataFrame(headerMeta, message));
    }

}
