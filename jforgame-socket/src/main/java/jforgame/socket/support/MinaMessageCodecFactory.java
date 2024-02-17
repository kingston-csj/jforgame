package jforgame.socket.support;

import jforgame.codec.MessageCodec;
import jforgame.socket.share.message.MessageFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author kinson
 */
public class MinaMessageCodecFactory implements ProtocolCodecFactory {

    private MinaProtocolDecoder decoder;

    private MinaProtocolEncoder encoder;

    public MinaMessageCodecFactory(MessageFactory messageFactory, MessageCodec messageCodec) {
        this.decoder = new MinaProtocolDecoder(messageFactory, messageCodec);
        this.encoder = new MinaProtocolEncoder(messageFactory, messageCodec);
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

}
