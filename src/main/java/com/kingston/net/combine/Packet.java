package com.kingston.net.combine;

import java.io.IOException;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.logs.LoggerUtils;
import com.kingston.net.Message;
import com.kingston.net.MessageFactory;

/**
 * message min unit
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

	//just for protobuf
	public Packet(){

	}

	public static Packet valueOf(Message message) {
		Packet packet  = new Packet();
		packet.module = message.getModule();
		packet.cmd        = message.getCmd();
		try {
			Codec codec = ProtobufProxy.create(message.getClass());
			packet.body = codec.encode(message);
		}catch (Exception e){
			throw new IllegalArgumentException("parse packet attachment failed",e);
		}

		return packet;
	}

	public static Message asMessage(Packet packet) {
		Class<?> msgClazz = MessageFactory.INSTANCE.getMessage((short)packet.module,  (short)packet.cmd);
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
			Message message = (Message) codec.decode(packet.body);
			return message;
		} catch (IOException e) {
			LoggerUtils.error("读取消息出错,模块号{}，类型{},异常{}",  packet.module,  packet.cmd );
		}
		return null;
	}

}
