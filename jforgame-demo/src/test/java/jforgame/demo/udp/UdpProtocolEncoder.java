package jforgame.demo.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class UdpProtocolEncoder extends MessageToMessageEncoder<UdpMessage> {
    private static final Logger logger = LoggerFactory.getLogger("socketserver");

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;


    public UdpProtocolEncoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, UdpMessage message, List<Object> out) throws Exception {
        // ----------------protocol pattern-------------------------
        // packetLength | cmd | body
        // int int byte[]
        int  cmd = messageFactory.getMessageId(message.getClass());
        try {
            byte[] body = messageCodec.encode(message);
            //消息内容长度
            ByteBuf buf = Unpooled.buffer(body.length+4);
            // 写入cmd类型
            buf.writeInt(cmd);
            buf.writeBytes(body);
            out.add(new DatagramPacket(buf, new InetSocketAddress(message.getReceiverIp(), message.getReceiverPort())));
        } catch (Exception e) {
            logger.error("wrote message {} failed", cmd, e);
        }
    }
}
