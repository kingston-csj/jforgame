package jforgame.socket.netty;

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
 * 协议栈解码器
 * 此类提供默认的私有协议栈解码器。
 * 一个完整的数据帧包含消息头（message head）和消息体（message body）两部分：
 * 消息头包含数据帧的长度（length of the data frame）和消息 ID 元数据（message id meta），消息序号（客户端自行管理）。
 * 消息体包括需要由{@link MessageCodec}解码的字节消息。
 *
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
