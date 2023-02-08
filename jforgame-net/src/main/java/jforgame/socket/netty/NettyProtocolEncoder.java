package jforgame.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jforgame.socket.CodecProperties;
import jforgame.socket.share.message.MessageEncoder;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.support.DefaultMessageCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyProtocolEncoder extends MessageToByteEncoder<Object> {

	private Logger logger = LoggerFactory.getLogger(NettyProtocolEncoder.class);

	private MessageFactory messageFactory;

	public NettyProtocolEncoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		// ----------------protocol pattern-------------------------
		// packetLength | cmd | body
		// int int byte[]

		int  cmd = messageFactory.getMessageId(message.getClass());
		try {
			final int metaSize = CodecProperties.MESSAGE_META_SIZE;
			MessageEncoder msgEncoder = DefaultMessageCodecFactory.getMessageCodecFactory().getEncoder();
			byte[] body = msgEncoder.writeMessageBody(message);
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
