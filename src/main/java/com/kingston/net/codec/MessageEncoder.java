package com.kingston.net.codec;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.kingston.logs.LoggerUtils;
import com.kingston.net.Message;
import com.kingston.net.MessageFactory;
import com.kingston.net.SessionProperties;

public class MessageEncoder implements ProtocolEncoder{

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		_encode(session, message, out);
	}

	public void _encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		CodecContext context = (CodecContext) session.getAttribute(SessionProperties.CODEC_CONTEXT);
		if (context == null) {
			context = new CodecContext();
			session.setAttribute(SessionProperties.CODEC_CONTEXT, context);
		}
		IoBuffer buffer = writeMessage((Message) message);
		out.write(buffer);
	}

	private IoBuffer writeMessage(Message message) {
		//----------------消息协议格式-------------------------
		// packetLength | moduleId | cmd   |  body
		// int            short      short   byte[]

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
		byte[] body = null;

		@SuppressWarnings("unchecked")
		Class<Message> msgClazz = (Class<Message>) MessageFactory.INSTANCE.getMessage(moduleId, cmd);
		try {
			Codec<Message> codec = ProtobufProxy.create(msgClazz);
			body = codec.encode(message);
		} catch (IOException e) {
			LoggerUtils.error("", e);
		}
		buffer.put(body);
		//回到buff字节数组头部
		buffer.flip();
		//重新写入包体长度
		buffer.putInt(buffer.limit()-4);
		buffer.rewind();

		return buffer;
	}

}
