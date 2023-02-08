package jforgame.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import jforgame.socket.CodecProperties;
import jforgame.socket.share.message.Message;
import jforgame.socket.share.message.MessageDecoder;
import jforgame.socket.support.DefaultMessageCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NettyProtocolDecoder extends ByteToMessageDecoder {

    private int maxReceiveBytes;

    private Logger logger = LoggerFactory.getLogger(NettyProtocolDecoder.class);

    public NettyProtocolDecoder(int maxReceiveBytes) {
        this.maxReceiveBytes = maxReceiveBytes;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        MessageDecoder msgDecoder = DefaultMessageCodecFactory.getMessageCodecFactory().getDecoder();
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
        final int metaSize = CodecProperties.MESSAGE_META_SIZE;
        int cmd = in.readInt();
        byte[] body = new byte[length - metaSize];
        in.readBytes(body);
        Object msg = msgDecoder.readMessage(cmd, body);

        out.add(msg);

    }

}
