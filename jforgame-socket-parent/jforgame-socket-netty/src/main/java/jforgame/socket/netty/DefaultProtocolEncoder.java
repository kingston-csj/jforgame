package jforgame.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.TrafficStatistic;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.SocketDataFrame;
import jforgame.socket.support.DefaultMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 协议栈编码器
 * 此类提供默认的私有协议栈编码器。
 * 一个完整的数据帧包含消息头（message head）和消息体（message body）两部分：
 * 消息头包含数据帧的长度（length of the data frame）和消息 ID 元数据（message id meta），消息序号（客户端自行管理）。
 * 消息体仅包含待编码的消息字节流，具体编码需通过 {@link MessageCodec} 接口的 {@link MessageCodec#encode (Object)} 方法实现。
 * 注意：此类标注了 {@link io.netty.channel.ChannelHandler.Sharable} 注解，因此可在不同的通道流水线（channel pipeline）中共享该编码器实例。
 * 如果使用共享对象，请确保{@link #messageCodec}实例线程安全
 */
@ChannelHandler.Sharable
public class DefaultProtocolEncoder extends MessageToByteEncoder<Object> {

    private static final Logger logger = LoggerFactory.getLogger("socketserver");

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;

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
            // 写入包头
            //消息内容长度
            int msgLength = body.length + DefaultMessageHeader.SIZE;
            out.writeInt(msgLength);
            out.writeInt(dataFrame.getIndex());
            // 写入cmd类型
            out.writeInt(cmd);

            // 写入包体
            out.writeBytes(body);

            // 流量统计
            TrafficStatistic.addSentBytes(cmd, msgLength);
            TrafficStatistic.addSentNumber(cmd);
        } catch (Exception e) {
            logger.error("wrote message {} failed", cmd, e);
        }
    }

}
