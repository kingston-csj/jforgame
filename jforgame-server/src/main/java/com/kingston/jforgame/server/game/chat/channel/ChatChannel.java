package com.kingston.jforgame.server.game.chat.channel;

import java.util.HashMap;
import java.util.Map;

import com.kingston.jforgame.server.game.chat.model.BaseChatMessage;

public abstract class ChatChannel {
	
	private static Map<ChannelType, ChatChannel> channels = new HashMap<>();
	
	static {
		channels.put(ChannelType.PRIVATE, new PrivatChatChannel());
	}

	public abstract ChannelType getChannelType();
	
	public static ChatChannel getChannel(ChannelType type) {
		return channels.get(type);
	}

	public abstract boolean verifySend(BaseChatMessage message);

	public void send(BaseChatMessage message) {
		if (verifySend(message)) {
			doSend(message);
		}
	}

	public void doSend(BaseChatMessage message) {

	}

}