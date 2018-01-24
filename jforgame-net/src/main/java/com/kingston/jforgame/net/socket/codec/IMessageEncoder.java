package com.kingston.jforgame.net.socket.codec;

import org.apache.mina.filter.codec.ProtocolEncoder;

import com.kingston.jforgame.net.socket.message.Message;

public interface IMessageEncoder extends ProtocolEncoder {

	byte[] writeMessageBody(Message message);

}
