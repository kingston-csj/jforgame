package jforgame.socket.mina.support;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.TrafficStatistic;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.SocketDataFrame;
import jforgame.socket.support.DefaultMessageHeader;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 协议栈解码器
 * 此类提供默认的私有协议栈解码器。
 * 一个完整的数据帧包含消息头（message head）和消息体（message body）两部分：
 * 消息头包含数据帧的长度（length of the data frame）和消息 ID 元数据（message id meta），消息序号（客户端自行管理）。
 * 消息体包括需要由{@link MessageCodec}解码的字节消息。
 *
 * @see MessageCodec#decode(Class, byte[])
 */
public class DefaultProtocolEncoder implements ProtocolEncoder {

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;

    private final int WRITE_BUFF_SIZE = 1024;

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

        // 写入包头
        //消息内容长度
        buffer.putInt(msgLength);
        buffer.putInt(frame.getIndex());
        // 写入cmd类型
        buffer.putInt(cmd);

        // 写入包体
        buffer.put(body);
        // 回到buff字节数组头部
        buffer.flip();

        // 流量统计
        TrafficStatistic.addSentBytes(cmd, msgLength);
        TrafficStatistic.addSentNumber(cmd);
        return buffer;
    }

}
