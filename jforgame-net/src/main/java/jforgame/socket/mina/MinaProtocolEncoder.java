package jforgame.socket.mina;

import jforgame.socket.CodecProperties;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.message.Message;
import jforgame.socket.message.MessageEncoder;
import jforgame.socket.message.MessageFactoryImpl;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

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
		// ----------------protocol pattern-------------------------
		// packetLength | cmd | body
		// int int byte[]

		IoBuffer buffer = IoBuffer.allocate(CodecProperties.WRITE_CAPACITY);
		buffer.setAutoExpand(true);

		MessageEncoder msgEncoder = SerializerHelper.getInstance().getEncoder();
		byte[] body = msgEncoder.writeMessageBody(message);
		final int metaSize = CodecProperties.MESSAGE_META_SIZE;
		// the length of message body
		buffer.putInt(body.length + metaSize);
		int cmd = MessageFactoryImpl.getInstance().getMessageId(message.getClass());
		// 写入cmd类型
		buffer.putInt(cmd);
		// 写入消息体
		buffer.put(body);
		// 回到buff字节数组头部
		buffer.flip();

		return buffer;
	}

}
