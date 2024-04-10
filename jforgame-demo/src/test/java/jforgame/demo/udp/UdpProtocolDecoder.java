package jforgame.demo.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import jforgame.codec.MessageCodec;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UdpProtocolDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;


    public UdpProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf in = msg.content();
        int length = in.readableBytes();
        int cmd = in.readInt();
        byte[] body = new byte[length - 4];
        in.readBytes(body);
        Class<?> msgClazz = messageFactory.getMessage(cmd);
        out.add(messageCodec.decode(msgClazz, body));
    }

}
