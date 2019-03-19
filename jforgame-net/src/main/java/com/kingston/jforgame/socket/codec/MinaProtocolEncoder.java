package com.kingston.jforgame.socket.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.kingston.jforgame.socket.message.Message;
import com.kingston.jforgame.socket.session.MinaSessionProperties;

/**
 * @author kingston
 */
public class MinaProtocolEncoder implements ProtocolEncoder {

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		CodecContext context = (CodecContext) session.getAttribute(MinaSessionProperties.CODEC_CONTEXT);
		if (context == null) {
			context = new CodecContext();
			session.setAttribute(MinaSessionProperties.CODEC_CONTEXT, context);
		}
		IoBuffer buffer = writeMessage((Message) message);
		out.write(buffer);
	}

	private IoBuffer writeMessage(Message message) {
		//----------------消息协议格式-------------------------
		// packetLength | moduleId | cmd   |  body
		//       int       short     short    byte[]

		IoBuffer buffer = IoBuffer.allocate(CodecContext.WRITE_CAPACITY);
		buffer.setAutoExpand(true);

		//消息内容长度，先占个坑
		buffer.putInt(0);
		short moduleId = message.getModule();
		short cmd = message.getCmd();
		//写入module类型
		buffer.putShort(moduleId);
		//写入cmd类型
		buffer.putShort(cmd);

		//写入具体消息的内容
		IMessageEncoder msgEncoder = SerializerHelper.getInstance().getEncoder();
		byte[] body = msgEncoder.writeMessageBody(message);
		buffer.put(body);
		//回到buff字节数组头部
		buffer.flip();
		//消息元信息，两个short，共4个字节
		final int metaSize = 4;
		//重新写入包体长度
		buffer.putInt(buffer.limit() - metaSize);
		buffer.rewind();

		return buffer;
	}

}
