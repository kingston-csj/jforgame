package jforgame.server.game.chat.channel;

import java.util.HashMap;
import java.util.Map;

import jforgame.server.game.chat.model.BaseChatMessage;

public abstract class ChatChannel {
	
	private static Map<ChannelType, ChatChannel> channels = new HashMap<>();
	
	public abstract ChannelType getChannelType();
	
	static {
		channels.put(ChannelType.PRIVATE, new PrivateChatChannel());
	}
	
	public static ChatChannel getChannel(ChannelType type) {
		return channels.get(type);
	}

	public abstract boolean verifySend(BaseChatMessage message);

	public void send(BaseChatMessage message) {
		if (verifySend(message)) {
			doSend(message);
		}
	}

	public abstract void doSend(BaseChatMessage message);

}