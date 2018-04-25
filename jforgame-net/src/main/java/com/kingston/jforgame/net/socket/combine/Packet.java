package com.kingston.jforgame.net.socket.combine;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.net.socket.codec.IMessageDecoder;
import com.kingston.jforgame.net.socket.codec.IMessageEncoder;
import com.kingston.jforgame.net.socket.codec.SerializerHelper;
import com.kingston.jforgame.net.socket.message.Message;

/**
 * full message unit
 * @author kingston
 */
public class Packet {

	@Protobuf(order = 10)
	private int module;
	@Protobuf(order = 11)
	private int cmd;
	@Protobuf(order = 12,fieldType = FieldType.BYTES)
	/** body of each message */
	private byte[] body ;


	public Packet(){
		//just for protobuf
	}

	public static Packet valueOf(Message message) {
		Packet packet  = new Packet();
		packet.module  = message.getModule();
		packet.cmd     = message.getCmd();

		IMessageEncoder msgEncoder = SerializerHelper.getInstance().getEncoder();
		packet.body = msgEncoder.writeMessageBody(message);

		return packet;
	}

	public static Message asMessage(Packet packet) {
		IMessageDecoder msgEncoder = SerializerHelper.getInstance().getDecoder();

		return msgEncoder.readMessage((short)packet.module, (short)packet.cmd, packet.body);
	}

}
