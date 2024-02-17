package jforgame.socket.support;

import jforgame.socket.share.message.MessageCodecFactory;
import jforgame.socket.codec.StructPrivateProtocolCodec;
import jforgame.socket.share.message.MessageFactory;

public class DefaultMessageCodecFactory {

    private static MessageCodecFactory messageCodecFactory = new StructPrivateProtocolCodec();

    public static MessageCodecFactory getMessageCodecFactory() {
        return messageCodecFactory;
    }
}
