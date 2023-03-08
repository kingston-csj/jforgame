package jforgame.socket.support;

import jforgame.socket.codec.MessageCodecFactory;
import jforgame.socket.codec.StructPrivateProtocolCodec;
import jforgame.socket.share.message.MessageFactory;

public class DefaultMessageCodecFactory {

    private static MessageFactory messageFactory = new MessageFactoryImpl();

    private static MessageCodecFactory messageCodecFactory = new StructPrivateProtocolCodec(messageFactory);


    public static MessageCodecFactory getMessageCodecFactory() {
        return messageCodecFactory;
    }
}
