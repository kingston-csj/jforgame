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
 * This class provides a default private protocol stack encoder.
 * A full data frame includes a message head and a message body
 * The message head including the length of the data frame and the message id meta.
 * If you want to contain other message meta, like the index of message, you need to store it in the message body.
 * The message body including just the bytes of message which needs to be encoded by {@link MessageCodec}
 * @see MessageCodec#encode(Object)
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
		Object message1 = frame.getMessage();
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
