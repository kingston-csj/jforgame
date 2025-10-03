package jforgame.socket.mina;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.TrafficStatistic;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.RequestDataFrame;
import jforgame.socket.support.DefaultMessageHeader;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 协议栈编码器
 * 此类提供默认的私有协议栈编码器。
 * 一个完整的数据帧包含消息头（message head）和消息体（message body）两部分：
 * 消息头包含数据帧的长度（length of the data frame）和消息 ID 元数据（message id meta），消息序号（客户端自行管理）。
 * 消息体仅包含待编码的消息字节流，具体编码需通过 {@link MessageCodec} 接口的 {@link MessageCodec#encode (Object)} 方法实现。
 */
public class DefaultProtocolDecoder extends CumulativeProtocolDecoder {

    private final Logger logger = LoggerFactory.getLogger("socketserver");
    /**
     * 最大的协议数据长度
     */
    private final int maxProtocolBytes;
    /**
     * 消息工厂
     */
    private MessageFactory messageFactory;
    /**
     * 消息解码器
     */
    private MessageCodec messageCodec;

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

        // 流量统计
        TrafficStatistic.addReceivedBytes(cmd, length);
        TrafficStatistic.addReceivedNumber(cmd);

        Class<?> msgClazz = messageFactory.getMessage(cmd);
        Object msg = messageCodec.decode(msgClazz, body);

        out.write(new RequestDataFrame(headerMeta, msg));
        return true;
    }

}
