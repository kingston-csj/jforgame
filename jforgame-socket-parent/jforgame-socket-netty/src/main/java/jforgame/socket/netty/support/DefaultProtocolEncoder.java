package jforgame.socket.netty.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.TrafficStatistic;
import jforgame.socket.share.message.MessageFactory;
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

	private final Logger logger = LoggerFactory.getLogger(DefaultProtocolEncoder.class);

	private final MessageFactory messageFactory;

	private final MessageCodec messageCodec;

	/**
	 * 消息元信息常量，为int类型的长度，表示消息的id
	 */
	private static final int MESSAGE_META_SIZE = 4;

	public DefaultProtocolEncoder(MessageFactory messageFactory, MessageCodec messageCodec) {
		this.messageFactory = messageFactory;
		this.messageCodec = messageCodec;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		// ----------------protocol pattern-------------------------
		// packetLength | cmd | body
		// int int byte[]
		int  cmd = messageFactory.getMessageId(message.getClass());
		try {
            byte[] body = messageCodec.encode(message);
			//消息内容长度
			int msgLength = body.length + MESSAGE_META_SIZE;
			out.writeInt(msgLength);

			// 流量统计
			TrafficStatistic.addSentBytes(cmd, msgLength);
			TrafficStatistic.addSentNumber(cmd);
			// 写入cmd类型
			out.writeInt(cmd);
			out.writeBytes(body);
		} catch (Exception e) {
			logger.error("wrote message {} failed", cmd, e);
		}
	}

}
