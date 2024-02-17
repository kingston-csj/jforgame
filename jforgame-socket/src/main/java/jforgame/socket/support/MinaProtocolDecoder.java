package jforgame.socket.support;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.message.MessageFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kinson
 */
public class MinaProtocolDecoder extends CumulativeProtocolDecoder {

	private Logger logger = LoggerFactory.getLogger(MinaProtocolDecoder.class);

	private int maxProtocolLength = 4096;

	private MessageFactory messageFactory;

	private MessageCodec messageCodec;

	/**
	 * 消息元信息常量，为int类型的长度，表示消息的id
	 */
	private static final int MESSAGE_META_SIZE = 4;

	public MinaProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec) {
		this.messageFactory = messageFactory;
		this.messageCodec = messageCodec;
	}


	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		if (in.remaining() < 4) {
			return false;
		}
		in.mark();

		// ----------------protocol pattern-------------------------
		// packetLength | cmd | body
		// int int byte[]
		int length = in.getInt();
		if (length > maxProtocolLength) {
			logger.error("单包长度[{}]过大，断开链接", length);
			session.close(true);
			return true;
		}

		if (in.remaining() < length) {
			in.reset();
			return false;
		}

		final int metaSize = MESSAGE_META_SIZE;
		int cmd = in.getInt();
		byte[] body = new byte[length - metaSize];
		in.get(body);

		Class<?> msgClazz = messageFactory.getMessage(cmd);
		Object msg = messageCodec.decode(msgClazz, body);

		out.write(msg);
		return true;
	}

}
