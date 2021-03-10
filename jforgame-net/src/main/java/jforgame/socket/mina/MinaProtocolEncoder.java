package jforgame.socket.mina;

import jforgame.socket.message.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import jforgame.socket.codec.IMessageEncoder;
import jforgame.socket.codec.SerializerHelper;

/**
 * @author kinson
 */
public class MinaProtocolEncoder implements ProtocolEncoder {

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = writeMessage((Message) message);
		out.write(buffer);
	}

	private IoBuffer writeMessage(Message message) {
		// ----------------消息协议格式-------------------------
		// packetLength | moduleId | cmd  | body
		// int             short     byte  byte[]

		IoBuffer buffer = IoBuffer.allocate(CodecProperties.WRITE_CAPACITY);
		buffer.setAutoExpand(true);

		IMessageEncoder msgEncoder = SerializerHelper.getInstance().getEncoder();
		// 具体消息编码
		byte[] body = msgEncoder.writeMessageBody(message);
		// 消息元信息常量3表示消息body前面的两个字段，一个short表示module，一个byte表示cmd,
		final int metaSize = CodecProperties.MESSAGE_META_SIZE;
		// 消息内容长度
		buffer.putInt(body.length + metaSize);
		short moduleId = message.getModule();
		byte cmd = message.getCmd();
		// 写入module类型
		buffer.putShort(moduleId);
		// 写入cmd类型
		buffer.put(cmd);
		// 写入消息体
		buffer.put(body);
		// 回到buff字节数组头部
		buffer.flip();

		return buffer;
	}

}
