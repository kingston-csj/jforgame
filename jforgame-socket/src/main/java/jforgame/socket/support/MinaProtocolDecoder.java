package jforgame.socket.support;

import jforgame.socket.CodecProperties;
import jforgame.socket.share.message.MessageDecoder;
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

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		if (in.remaining() < 4) {
			return false;
		}
		MessageDecoder msgDecoder = DefaultMessageCodecFactory.getMessageCodecFactory().getDecoder();
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

		final int metaSize = CodecProperties.MESSAGE_META_SIZE;
		int cmd = in.getInt();
		byte[] body = new byte[length - metaSize];
		in.get(body);
		Object msg = msgDecoder.readMessage(cmd, body);

		out.write(msg);
		return true;
	}

}
