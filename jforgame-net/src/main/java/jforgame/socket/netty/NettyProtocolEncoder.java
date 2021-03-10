package jforgame.socket.netty;

import jforgame.socket.codec.IMessageEncoder;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyProtocolEncoder extends MessageToByteEncoder<Message> {

	private Logger logger = LoggerFactory.getLogger(NettyProtocolEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
		// ----------------消息协议格式-------------------------
		// packetLength | moduleId | cmd | body
		// short           short     byte   byte[]
		// 其中 packetLength长度占2位，由编码链 LengthFieldPrepender(2) 提供

		short module = message.getModule();
		byte cmd = message.getCmd();

		try {
			// 消息元信息常量3表示消息body前面的两个字段，一个short表示module，一个byte表示cmd,
			final int metaSize = 3;
			IMessageEncoder msgEncoder = SerializerHelper.getInstance().getEncoder();
			byte[] body = msgEncoder.writeMessageBody(message);
			//消息内容长度
			out.writeInt(body.length + metaSize);
			// 写入module类型
			out.writeShort(module);
			// 写入cmd类型
			out.writeByte(cmd);
			out.writeBytes(body);
		} catch (Exception e) {
			logger.error("读取消息出错,模块号{}，类型{},异常{}", new Object[] { module, cmd, e });
		}

	}

}
