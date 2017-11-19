package com.kingston.jforgame.net.socket.combine;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.net.socket.message.Message;
import com.kingston.jforgame.net.socket.message.MessageFactory;

/**
 * full message unit
 * @author kingston
 */
public class Packet {

	private static Logger logger = LoggerFactory.getLogger(Packet.class);

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
			logger.error("读取消息出错,模块号{}，类型{},异常{}",  new Object[]{packet.module,  packet.cmd} );
		}
		return null;
	}

}
