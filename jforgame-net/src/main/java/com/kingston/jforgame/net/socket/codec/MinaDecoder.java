package com.kingston.jforgame.net.socket.codec;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.kingston.jforgame.net.socket.combine.CombineMessage;
import com.kingston.jforgame.net.socket.combine.Packet;
import com.kingston.jforgame.net.socket.message.Message;
import com.kingston.jforgame.net.socket.session.SessionManager;
import com.kingston.jforgame.net.socket.session.SessionProperties;

/**
 * @author kingston
 */
public class MinaDecoder implements ProtocolDecoder {

	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		//必须保证每一个数据包的字节缓存都和session绑定在一起，不然就读取不了上一次剩余的数据了
		CodecContext context = SessionManager.INSTANCE.getSessionAttr(session, SessionProperties.CODEC_CONTEXT, CodecContext.class);
		if (context == null) {
			context = new CodecContext();
			session.setAttribute(SessionProperties.CODEC_CONTEXT, context);
		}

		IoBuffer ioBuffer = context.getBuffer();
		ioBuffer.put(in);

		IMessageDecoder msgDecoder = SerializerHelper.getInstance().getDecoder();

		//在循环里迭代，以处理数据粘包
		for (; ;) {
			ioBuffer.flip();
			//消息元信息常量4表示消息body前面的两个short字段，一个表示module，一个表示cmd,
			final int metaSize = 4;
			if (ioBuffer.remaining() < metaSize) {
				ioBuffer.compact();
				return;
			}
			//----------------消息协议格式-------------------------
			// packetLength | moduleId | cmd   |  body
			//       int       short     short    byte[]
			int length = ioBuffer.getInt();
			//int packLen = length + 4;
			//大于消息body长度，说明至少有一条完整的message消息
			if (ioBuffer.remaining() >= length) {
				short moduleId =  ioBuffer.getShort();
				short cmd = ioBuffer.getShort();
				byte[] body = new byte[length-metaSize];
				ioBuffer.get(body);
				Message msg = msgDecoder.readMessage(moduleId, cmd, body);

				if (moduleId > 0) {
					out.write(msg);
				} else { //属于组合包
					CombineMessage combineMessage = (CombineMessage)msg;
					List<Packet> packets = combineMessage.getPackets();
					for (Packet packet :packets) {
						//依次拆包反序列化为具体的Message
						out.write(Packet.asMessage(packet));
					}
				}
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

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {

	}
}
