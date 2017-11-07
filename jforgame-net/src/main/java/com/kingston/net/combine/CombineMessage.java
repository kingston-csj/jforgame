package com.kingston.net.combine;

import java.util.ArrayList;
import java.util.List;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.net.annotation.MessageMeta;
import com.kingston.net.message.Message;

/**
 * special message used to combine lots of messages together
 * @author kingston
 */
@MessageMeta()
public final class CombineMessage extends Message {

	@Protobuf(order = 1)
	private final List<Packet> packets = new ArrayList<>();

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
