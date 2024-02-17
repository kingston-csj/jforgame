package jforgame.socket.codec;

import jforgame.socket.codec.struct.StructMessageDecoder;
import jforgame.socket.codec.struct.StructMessageEncoder;
import jforgame.socket.share.message.MessageCodecFactory;
import jforgame.socket.share.message.MessageFactory;

public class StructPrivateProtocolCodec implements MessageCodecFactory {

    private MessageDecoder decoder;

    private MessageEncoder encoder;

    public StructPrivateProtocolCodec() {
        this.decoder = new StructMessageDecoder();
        this.encoder = new StructMessageEncoder();
    }

    @Override
    public MessageDecoder getDecoder() {
        return decoder;
    }

    @Override
    public MessageEncoder getEncoder() {
        return encoder;
    }

}
