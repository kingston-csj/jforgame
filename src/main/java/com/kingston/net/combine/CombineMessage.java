package com.kingston.net.combine;

import java.util.ArrayList;
import java.util.List;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.net.Message;
import com.kingston.net.annotation.Protocol;

@Protocol(module=0, cmd=0)
public final class CombineMessage extends Message {
	
	@Protobuf(order = 1)
	private final List<Packet> packets = new ArrayList<>();

	public CombineMessage(){

	}

	/**
	 * 添加新的消息对象
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
