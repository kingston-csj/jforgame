package jforgame.socket.netty;

import java.util.List;

import jforgame.socket.codec.IMessageDecoder;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.combine.CombineMessage;
import jforgame.socket.combine.Packet;
import jforgame.socket.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        IMessageDecoder msgDecoder = SerializerHelper.getInstance().getDecoder();
        in.markReaderIndex();
        // ----------------消息协议格式-------------------------
        // packetLength | moduleId | cmd | body
        // int short short byte[]
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
        // 消息元信息常量3表示消息body前面的两个字段，一个short表示module，一个byte表示cmd,
        final int metaSize = 3;
        short moduleId = in.readShort();
        byte cmd = in.readByte();
        byte[] body = new byte[length - metaSize];
        in.readBytes(body);
        Message msg = msgDecoder.readMessage(moduleId, cmd, body);

        if (moduleId > 0) {
            out.add(msg);
        } else { // 属于组合包
            CombineMessage combineMessage = (CombineMessage) msg;
            List<Packet> packets = combineMessage.getPackets();
            for (Packet packet : packets) {
                // 依次拆包反序列化为具体的Message
                out.add(Packet.asMessage(packet));
            }
        }
    }

}
