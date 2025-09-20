package jforgame.codec.struct;

import jforgame.codec.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 基于bean结构体的消息体编码解码
 * 1. 支持基本类型
 * 2. 支持数组类型
 * 3. 支持集合类型
 * 4. 支持自定义类型
 */
public class StructMessageCodec implements MessageCodec {

    private static final Logger logger = LoggerFactory.getLogger(StructMessageCodec.class);

    private static final int DEFAULT_WRITE_BUFF_SIZE = 1024 * 1024; // 1M

    private final ThreadLocal<ByteBuffer> localBuff;

    /**
     * @param writeBuffSize max buff size to encode a message bean
     *                      if body size of a message exceed than the writeBuffSize, {@link java.nio.BufferOverflowException} exception will be thrown
     */
    public StructMessageCodec(int writeBuffSize) {
        this.localBuff = ThreadLocal.withInitial(() -> ByteBuffer.allocate(writeBuffSize));
    }

    public StructMessageCodec() {
        this(DEFAULT_WRITE_BUFF_SIZE);
    }

    public Object decode(Class<?> msgClazz, byte[] body) {
        // 消息序列化这里的buff已经是一个完整的包体
        ByteBuffer in = ByteBuffer.allocate(body.length);
        in.put(body);
        in.flip();

        try {
            Codec messageCodec = Codec.getSerializer(msgClazz);
            return messageCodec.decode(in, msgClazz, null);
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
            logger.error("read message failed ", e);
        }
        allocator.flip();
        byte[] body = new byte[allocator.remaining()];
        allocator.get(body);
        return body;
    }

}
