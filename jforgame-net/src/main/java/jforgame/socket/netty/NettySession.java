package jforgame.socket.netty;

import java.util.HashMap;
import java.util.Map;

import jforgame.socket.IdSession;
import jforgame.socket.message.Message;

import io.netty.channel.Channel;

public class NettySession implements IdSession {
	
	/** 网络连接channel */
	private Channel channel;
	
	/** 拓展用，保存一些个人数据  */
	private Map<String, Object> attrs = new HashMap<>();

	public NettySession(Channel channel) {
		super();
		this.channel = channel;
	}

	@Override
	public void sendPacket(Message packet) {
		channel.writeAndFlush(packet);
	}

	@Override
	public long getOwnerId() {
		if (attrs.containsKey(ID)) {
			return (long) attrs.get(ID);
		}
		return 0;
	}

	@Override
	public Object setAttribute(String key, Object value) {
		attrs.put(key, value);
		return value;
	}

	@Override
	public Object getAttribute(String key) {
		return attrs.get(key);
	}

}
