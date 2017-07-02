package com.kingston.net.codec;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.kingston.logs.LoggerSystem;
import com.kingston.net.Message;
import com.kingston.net.MessageFactory;
import com.kingston.net.SessionProperties;

public class MessageDecoder implements ProtocolDecoder{
	
	private static final Logger logger = LoggerSystem.EXCEPTION.getLogger();

	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		_decode(session, in, out);

	}

	private void _decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
		//必须保证每一个数据包的字节缓存都和session绑定在一起，不然就读取不了上一次剩余的数据了
		CodecContext context = (CodecContext) session.getAttribute(SessionProperties.CONTEXT_KEY);
		if (context == null) {
			context = new CodecContext();
			session.setAttribute(SessionProperties.CONTEXT_KEY, context);
		}
		IoBuffer ioBuffer = context.getBuffer();
		ioBuffer.put(in);

		//在循环里迭代，以处理数据粘包
		for (; ;) {
			ioBuffer.flip();
			//常量4表示消息body前面的两个short字段，一个表示moduel，一个表示cmd,
			//一个short字段有两个字节，总共4个字节
			if (ioBuffer.remaining() < 4) {
				ioBuffer.compact();
				return;
			}
			//----------------消息协议格式-------------------------
			// packetLength | moduleId | cmd   |  body
			// int            short      short   byte[]
			int length = ioBuffer.getInt();
			int packLen = length + 4;
			//大于消息body长度，说明至少有一条完整的message消息
			if (ioBuffer.remaining() >= length) {
				short moduleId =  ioBuffer.getShort();
				short cmd = ioBuffer.getShort();
				byte[] body = new byte[length-4];
				ioBuffer.get(body);

				Message msg = readMessage(moduleId, cmd, body);
				out.write(msg);

				if (ioBuffer.remaining() == 0) {
					ioBuffer.clear();
					break;
				}
				ioBuffer.compact();
			} else{
				//数据包不完整，继续等待数据到达
				ioBuffer.rewind();
				ioBuffer.compact();
				break;
			}
		}
	}

	private Message readMessage(short module, short cmd, byte[] body) {
		Class<?> msgClazz = MessageFactory.INSTANCE.getMessage(module, cmd);
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
			Message message = (Message) codec.decode(body);
			return message;
		} catch (IOException e) {
			logger.error("读取消息出错,模块号{}，类型{},异常{}", new Object[]{module, cmd ,e});
		}
		return null;
	}

	public void dispose(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {
		// TODO Auto-generated method stub

	}

}
