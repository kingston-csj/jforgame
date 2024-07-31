package jforgame.socket.netty.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.TrafficStatistic;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.MessageHeader;
import jforgame.socket.share.message.SocketDataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a default private protocol stack encoder.
 * A full data frame includes a message head and a message body
 * The message head including the length of the data frame and the message id meta.
 * If you want to contain other message meta, like the index of message, you need to store it in the message body.
 * The message body including just the bytes of message which needs to be encoded by {@link MessageCodec}
 * @see MessageCodec#encode(Object)
 *
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
		int  cmd = messageFactory.getMessageId(dataFrame.getMessage().getClass());
		try {
            byte[] body = messageCodec.encode(dataFrame.getMessage());
			// 写入包头
			//消息内容长度
			int msgLength = body.length + MessageHeader.SIZE;
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
