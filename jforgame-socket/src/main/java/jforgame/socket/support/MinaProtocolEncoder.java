package jforgame.socket.support;

import jforgame.socket.CodecProperties;
import jforgame.socket.codec.MessageEncoder;
import jforgame.socket.share.message.MessageFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @author kinson
 */
public class MinaProtocolEncoder implements ProtocolEncoder {

	private MessageFactory messageFactory;

	public MinaProtocolEncoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = writeMessage(message);
		out.write(buffer);
	}

	private IoBuffer writeMessage(Object message) {
		// ----------------protocol pattern-------------------------
		// packetLength | cmd | body
		// int int byte[]

		IoBuffer buffer = IoBuffer.allocate(CodecProperties.WRITE_CAPACITY);
		buffer.setAutoExpand(true);

		MessageEncoder msgEncoder = DefaultMessageCodecFactory.getMessageCodecFactory().getEncoder();
		byte[] body = msgEncoder.writeMessageBody(message);
		final int metaSize = CodecProperties.MESSAGE_META_SIZE;
		// the length of message body
		buffer.putInt(body.length + metaSize);
		int cmd = messageFactory.getMessageId(message.getClass());
		// 写入cmd类型
		buffer.putInt(cmd);
		// 写入消息体
		buffer.put(body);
		// 回到buff字节数组头部
		buffer.flip();

		return buffer;
	}

}
