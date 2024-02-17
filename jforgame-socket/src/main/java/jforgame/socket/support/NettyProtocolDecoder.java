package jforgame.socket.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NettyProtocolDecoder extends ByteToMessageDecoder {

    private int maxReceiveBytes = 4096;

    private Logger logger = LoggerFactory.getLogger(NettyProtocolDecoder.class);

    private MessageFactory messageFactory;

    private MessageCodec messageCodec;

    /**
     * 消息元信息常量，为int类型的长度，表示消息的id
     */
    private static final int MESSAGE_META_SIZE = 4;

    public NettyProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        // ----------------protocol pattern-------------------------
        // packetLength | cmd | body
        // int int byte[]
        int length = in.readInt();
        if (length > maxReceiveBytes) {
            logger.error("单包长度[{}]过大，断开链接", length);
            ctx.close();
            return;
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        final int metaSize = MESSAGE_META_SIZE;
        int cmd = in.readInt();
        byte[] body = new byte[length - metaSize];
        in.readBytes(body);

        Class<?> msgClazz = messageFactory.getMessage(cmd);
        Object msg = messageCodec.decode(msgClazz, body);

        out.add(msg);

    }

}
