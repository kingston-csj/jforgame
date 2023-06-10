package jforgame.socket.codec;

import jforgame.socket.codec.struct.StructMessageDecoder;
import jforgame.socket.codec.struct.StructMessageEncoder;
import jforgame.socket.share.message.MessageDecoder;
import jforgame.socket.share.message.MessageEncoder;
import jforgame.socket.share.message.MessageFactory;

public class StructPrivateProtocolCodec implements MessageCodecFactory {

    private MessageDecoder decoder;

    private MessageEncoder encoder;

    public StructPrivateProtocolCodec(MessageFactory messageFactory) {
        this.decoder = new StructMessageDecoder(messageFactory);
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
