package jforgame.socket.mina;

import jforgame.socket.CodecProperties;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.message.Message;
import jforgame.socket.message.MessageDecoder;
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

	private int maxProtocolLength;

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		if (in.remaining() < 4) {
			return false;
		}
		MessageDecoder msgDecoder = SerializerHelper.getInstance().getDecoder();
		in.mark();

		// ----------------protocol pattern-------------------------
		// packetLength | cmd | body
		// int int byte[]
		int length = in.getInt();
		int maxReceiveBytes = 4096;
		if (length > maxReceiveBytes) {
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
		Message msg = msgDecoder.readMessage(cmd, body);

		out.write(msg);
		return true;
	}

}
