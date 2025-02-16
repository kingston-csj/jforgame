package jforgame.socket.netty.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.TrafficStatistic;
import jforgame.socket.share.message.Message;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.SCMessage;
import jforgame.socket.share.message.SocketDataFrame;
import jforgame.socket.support.DefaultMessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a default private protocol stack encoder.
 * A full data frame includes a message head and a message body
 * The message head including the length of the data frame and the message id meta.
 * If you want to contain other message meta, like the index of message, you need to store it in the message body.
 * The message body including just the bytes of message which needs to be encoded by {@link MessageCodec}
 *
 * @see MessageCodec#encode(Object)
 * <p>
 * Remeber this class is annotationed by {@link io.netty.channel.ChannelHandler.Sharable}, you can share the encoder
 * in different channel pipeline.
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
        // 不从外部传入cmd，而是通过message对象获取cmd
        SCMessage scMessage = (SCMessage) dataFrame.getMessage();
        int errorCode = scMessage.getErrorCode();
        int cmd = scMessage.getCmd();
        if (errorCode != 0) {
            out.writeInt(DefaultMessageHeader.SIZE + 4);
            out.writeInt(dataFrame.getIndex());
            out.writeInt(cmd);
            out.writeInt(errorCode);
        } else {
            //int  cmd = messageFactory.getMessageId(dataFrame.getMessage().getClass());
            try {
                byte[] body = messageCodec.encode(scMessage);
                // 写入包头
                //消息内容长度
                int msgLength = body.length + DefaultMessageHeader.SIZE;
                out.writeInt(msgLength);
                out.writeInt(dataFrame.getIndex());
                // 写入cmd类型
                out.writeInt(cmd);
                out.writeInt(0);
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

}
