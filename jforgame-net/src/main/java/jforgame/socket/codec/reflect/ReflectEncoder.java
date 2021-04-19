package jforgame.socket.codec.reflect;

import jforgame.socket.codec.IMessageEncoder;
import jforgame.socket.message.Message;
import jforgame.socket.mina.CodecProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class ReflectEncoder implements IMessageEncoder {

    private static Logger logger = LoggerFactory.getLogger(ReflectEncoder.class);

    private ThreadLocal<ByteBuffer> localBuff = ThreadLocal.withInitial(() -> ByteBuffer.allocate(CodecProperties.WRITE_CAPACITY));

    @Override
    public byte[] writeMessageBody(Message message) {
        ByteBuffer allocator = localBuff.get();
        allocator.clear();

        //写入具体消息的内容
        try {
            Codec messageCodec = Codec.getSerializer(message.getClass());
            messageCodec.encode(allocator, message, null);
        } catch (Exception e) {
            logger.error("读取消息出错,模块号{}，类型{},异常{}",
                    new Object[]{message.getModule(), message.getCmd(), e});
        }
        allocator.flip();
        byte[] body = new byte[allocator.remaining()];
        allocator.get(body);
        return body;
    }

}
