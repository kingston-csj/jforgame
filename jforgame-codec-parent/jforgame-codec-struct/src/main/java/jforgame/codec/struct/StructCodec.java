package jforgame.codec.struct;

import jforgame.codec.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Message encoding and decoding based on bean structure.
 * 1. Supports basic types
 * 2. Supports array types
 * 3. Supports collection types
 * 4. Supports custom types
 * By default, collection elements must strictly follow the homogeneous mode, meaning all elements must be exactly the same as the wrapper type, subclasses/heterogeneous types are not allowed.
 * If subclass/implementation of wrapper is allowed, the constructor useUpgradeVersion must be set to true, and since collection elements may not be homogeneous, you need {@link LiteMessageFactory} to record the actual type of all elements.
 */
public class StructCodec implements MessageCodec {

    private static final Logger logger = LoggerFactory.getLogger(StructCodec.class);

    private static final int DEFAULT_WRITE_BUFF_SIZE = 1024 * 1024; // 1M

    private final ThreadLocal<ByteBuffer> localBuff;

    /**
     * Create a message codec
     *
     * @param writeBuffSize max length after encoding a single message
     * @param useUpgradeVersion whether to enable upgrade version, if true, collection elements can use inheritance mode
     */
    public StructCodec(int writeBuffSize, boolean useUpgradeVersion) {
        this.localBuff = ThreadLocal.withInitial(() -> ByteBuffer.allocate(writeBuffSize));
        if (useUpgradeVersion) {
            StructCodecEnvironment.collectionSerializeMode = CollectionSerializeMode.SUB_CLASS_POLYMORPHIC;
            if (StructCodecEnvironment.messageFactory == null) {
                throw new IllegalArgumentException("messageFactory is null");
            }
            // Switch collection related codecs
            this.upgradeCodec();
        }
    }

    private void upgradeCodec() {
        Codec.replace(List.class, new CollectionCodec2());
        Codec.replace(Set.class, new CollectionCodec2());
        Codec.replace(Object[].class, new ArrayCodec2());
        Codec.replace(Map.class, new MapCodec2());
    }

    /**
     * @param writeBuffSize max buff size to encode a message bean
     *                      if body size of a message exceed than the writeBuffSize, {@link java.nio.BufferOverflowException} exception will be thrown
     */
    public StructCodec(int writeBuffSize) {
        this(writeBuffSize, false);
    }

    public StructCodec() {
        this(DEFAULT_WRITE_BUFF_SIZE, false);
    }

    public Object decode(Class<?> msgClazz, byte[] body) {
        // The buffer here is already a complete package
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
