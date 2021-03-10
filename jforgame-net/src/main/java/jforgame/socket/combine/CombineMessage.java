package jforgame.socket.combine;

import java.util.ArrayList;
import java.util.List;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

/**
 * special message used to combine lots of messages together
 * @author kinson
 */
@MessageMeta()
public final class CombineMessage extends Message {

	@Protobuf(order = 1)
	private List<Packet> packets = new ArrayList<>();

	public CombineMessage(){

	}

	/**
	 * add new message to combine queue
	 * @param message
	 */
	public void addMessage(Message message){
		this.packets.add(Packet.valueOf(message));
	}

	public List<Packet> getPackets() {
		return packets;
	}

	public int getCacheSize(){
		return this.packets.size();
	}

}
