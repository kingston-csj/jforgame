package com.kingston.jforgame.socket.codec.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.kingston.jforgame.socket.codec.IMessageEncoder;
import com.kingston.jforgame.socket.codec.SerializerHelper;
import com.kingston.jforgame.socket.message.Message;
import com.kingston.jforgame.socket.mina.MinaSessionProperties;

/**
 * @author kingston
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

		IoBuffer buffer = IoBuffer.allocate(CodecContext.WRITE_CAPACITY);
		buffer.setAutoExpand(true);

		// 写入具体消息的内容
		IMessageEncoder msgEncoder = SerializerHelper.getInstance().getEncoder();
		byte[] body = msgEncoder.writeMessageBody(message);
		// 消息元信息常量3表示消息body前面的两个字段，一个short表示module，一个byte表示cmd,
		final int metaSize = 3;
		// 消息内容长度
		buffer.putInt(body.length + metaSize);
		short moduleId = message.getModule();
		byte cmd = message.getCmd();
		// 写入module类型
		buffer.putShort(moduleId);
		// 写入cmd类型
		buffer.put(cmd);
	
		buffer.put(body);
//		// 回到buff字节数组头部
		buffer.flip();

		return buffer;
	}

}
