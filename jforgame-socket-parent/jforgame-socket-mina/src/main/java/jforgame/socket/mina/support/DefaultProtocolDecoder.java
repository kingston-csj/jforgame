package jforgame.socket.mina.support;

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
public class DefaultProtocolDecoder extends CumulativeProtocolDecoder {

	private Logger logger = LoggerFactory.getLogger(DefaultProtocolDecoder.class);

	private int maxProtocolBytes;

	private MessageFactory messageFactory;

	private MessageCodec messageCodec;

	/**
	 * 消息元信息常量，为int类型的长度，表示消息的id
	 */
	private final int MESSAGE_META_SIZE = 4;

	public DefaultProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec) {
		this(messageFactory, messageCodec, 4096);
	}

	public DefaultProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec, int maxProtocolBytes) {
		this.messageFactory = messageFactory;
		this.messageCodec = messageCodec;
		this.maxProtocolBytes = maxProtocolBytes;
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
		if (length > maxProtocolBytes) {
			logger.error("message data frame [{}] too large, close session now", length);
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
