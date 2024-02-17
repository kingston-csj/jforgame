package jforgame.socket.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jforgame.socket.codec.MessageCodec;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyProtocolEncoder extends MessageToByteEncoder<Object> {

	private Logger logger = LoggerFactory.getLogger(NettyProtocolEncoder.class);

	private MessageFactory messageFactory;

	private MessageCodec messageCodec;

	/**
	 * 消息元信息常量，为int类型的长度，表示消息的id
	 */
	private static final int MESSAGE_META_SIZE = 4;

	public NettyProtocolEncoder(MessageFactory messageFactory, MessageCodec messageCodec) {
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
			final int metaSize = MESSAGE_META_SIZE;
			byte[] body = messageCodec.encode(message);
			//消息内容长度
			out.writeInt(body.length + metaSize);
			// 写入cmd类型
			out.writeInt(cmd);
			out.writeBytes(body);
		} catch (Exception e) {
			logger.error("wrote message {} failed", cmd, e);
		}

	}

}
