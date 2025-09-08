package jforgame.socket.netty.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.TrafficStatistic;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.RequestDataFrame;
import jforgame.socket.support.DefaultMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class provides a default private protocol stack decoder.
 * A full data frame includes a message head and a message body
 * The message head including the length of the data frame and the message id meta.
 * If you want to contain other message meta, like the index of message, you need to store it in the message body.
 * The message body including just the bytes of message which needs to be decoded by {@link MessageCodec}
 * @see MessageCodec#decode(Class, byte[])
 */
public class DefaultProtocolDecoder extends ByteToMessageDecoder {

    /**
     * 最大协议字节数（包头+包体）
     */
    private int maxProtocolBytes;

    private final Logger logger = LoggerFactory.getLogger("socketserver");

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;


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

        // 流量统计
        TrafficStatistic.addReceivedBytes(cmd, length);
        TrafficStatistic.addReceivedNumber(cmd);

        Class<?> msgClazz = messageFactory.getMessage(cmd);

        Object message = messageCodec.decode(msgClazz, body);
        out.add(new RequestDataFrame(headerMeta, message));
    }

}
