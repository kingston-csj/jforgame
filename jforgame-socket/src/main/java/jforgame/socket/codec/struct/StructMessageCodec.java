package jforgame.socket.codec.struct;

import jforgame.socket.codec.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class StructMessageCodec implements MessageCodec {

	private static Logger logger = LoggerFactory.getLogger(StructMessageCodec.class);

	private int WRITE_BUFF_SIZE = 1024;

	private ThreadLocal<ByteBuffer> localBuff = ThreadLocal.withInitial(() -> ByteBuffer.allocate(WRITE_BUFF_SIZE));


	public Object decode(Class<?> msgClazz, byte[] body) {
		// 消息序列化这里的buff已经是一个完整的包体
		ByteBuffer in = ByteBuffer.allocate(body.length);
		in.put(body);
		in.flip();
		
		try {
			Codec messageCodec = Codec.getSerializer(msgClazz);
			Object message =  messageCodec.decode(in, msgClazz, null);
			return message;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public byte[] encode(Object message) {
		ByteBuffer allocator = localBuff.get();
		allocator.clear();

		try {
			Codec messageCodec = Codec.getSerializer(message.getClass());
			messageCodec.encode(allocator, message, null);
		} catch (Exception e) {
			logger.error("read message {} failed , exception {}",
					new Object[]{message.getClass(), e});
		}
		allocator.flip();
		byte[] body = new byte[allocator.remaining()];
		allocator.get(body);
		return body;
	}

}
