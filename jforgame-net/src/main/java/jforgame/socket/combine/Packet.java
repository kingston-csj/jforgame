package jforgame.socket.combine;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.socket.codec.PrivateProtocolDecoder;
import jforgame.socket.codec.PrivateProtocolEncoder;
import jforgame.socket.codec.SerializerHelper;
import jforgame.socket.message.Message;

/**
 * full message unit
 * @author kinson
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

		PrivateProtocolEncoder msgEncoder = SerializerHelper.getInstance().getEncoder();
		packet.body = msgEncoder.writeMessageBody(message);

		return packet;
	}

	public static Message asMessage(Packet packet) {
		PrivateProtocolDecoder msgEncoder = SerializerHelper.getInstance().getDecoder();

		return msgEncoder.readMessage(packet.cmd, packet.body);
	}

}
