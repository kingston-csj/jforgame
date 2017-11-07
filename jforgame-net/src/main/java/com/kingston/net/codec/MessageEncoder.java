package com.kingston.net.codec;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.kingston.net.message.Message;
import com.kingston.net.message.MessageFactory;
import com.kingston.net.session.SessionProperties;

public class MessageEncoder implements ProtocolEncoder {

	private static Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
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
		byte[] body = wrapMessageBody(moduleId, cmd, message);
		buffer.put(body);
		//回到buff字节数组头部
		buffer.flip();
		//消息元信息，两个short，共4个字节
		final int METE_SIZE = 4;
		//重新写入包体长度
		buffer.putInt(buffer.limit() - METE_SIZE);
		buffer.rewind();

		return buffer;
	}

	private byte[] wrapMessageBody(short module, short cmd, Message message) {
		//写入具体消息的内容
		byte[] body = null;
		@SuppressWarnings("unchecked")
		Class<Message> msgClazz = (Class<Message>) MessageFactory.INSTANCE.getMessage(module, cmd);
		try {
			Codec<Message> codec = ProtobufProxy.create(msgClazz);
			body = codec.encode(message);
		} catch (IOException e) {
			logger.error("", e);
		}
		return body;
	}

}
