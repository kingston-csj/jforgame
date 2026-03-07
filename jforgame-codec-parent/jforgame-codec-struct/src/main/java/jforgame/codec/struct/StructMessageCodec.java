package jforgame.codec.struct;

import jforgame.codec.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基于bean结构体的消息体编码解码
 * 1. 支持基本类型
 * 2. 支持数组类型
 * 3. 支持集合类型
 * 4. 支持自定义类型
 * 默认情况下，集合的元素必须遵守严格同构模式，即所有元素必须与wrapper类型完全一致，不允许子类/异构
 * 若允许元素为wrapper的子类/实现类，则构造函数useUpgradeVersion必须设置为true，且由于集合元素可能不是同构元素，需要{@link LiteMessageFactory}记录所有元素的真实类型
 */
public class StructMessageCodec implements MessageCodec {

    private static final Logger logger = LoggerFactory.getLogger(StructMessageCodec.class);

    private static final int DEFAULT_WRITE_BUFF_SIZE = 1024 * 1024; // 1M

    private final ThreadLocal<ByteBuffer> localBuff;

    /**
     * 创建一个消息体编解码器
     *
     * @param writeBuffSize 单个消息体编码后的最大长度
     * @param useUpgradeVersion 是否启用升级版本，若为true,代表集合元素可以使用继承模式
     */
    public StructMessageCodec(int writeBuffSize, boolean useUpgradeVersion) {
        this.localBuff = ThreadLocal.withInitial(() -> ByteBuffer.allocate(writeBuffSize));
        if (useUpgradeVersion) {
            StructCodecEnvironment.collectionSerializeMode = CollectionSerializeMode.SUB_CLASS_POLYMORPHIC;
            if (StructCodecEnvironment.messageFactory == null) {
                throw new IllegalArgumentException("messageFactory is null");
            }
            // 切换集合相关编解码
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
    public StructMessageCodec(int writeBuffSize) {
        this(writeBuffSize, false);
    }

    public StructMessageCodec() {
        this(DEFAULT_WRITE_BUFF_SIZE, false);
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
