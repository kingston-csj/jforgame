package jforgame.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jforgame.socket.CodecProperties;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.message.Message;
import jforgame.socket.message.MessageEncoder;
import jforgame.socket.message.MessageFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyProtocolEncoder extends MessageToByteEncoder<Message> {

	private Logger logger = LoggerFactory.getLogger(NettyProtocolEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
		// ----------------protocol pattern-------------------------
		// packetLength | cmd | body
		// int int byte[]

		int  cmd = MessageFactoryImpl.getInstance().getMessageId(message.getClass());

		try {
			final int metaSize = CodecProperties.MESSAGE_META_SIZE;
			MessageEncoder msgEncoder = SerializerHelper.getInstance().getEncoder();
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
