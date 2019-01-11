package com.kingston.jforgame.server.game.chat;

import com.kingston.jforgame.server.game.chat.channel.ChannelType;
import com.kingston.jforgame.server.game.chat.channel.ChatChannel;
import com.kingston.jforgame.server.game.chat.model.TextChatMessage;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.player.PlayerManager;

public class ChatManager {
	
	private static volatile ChatManager instance;
	
	public static ChatManager getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (ChatManager.class) {
			if (instance == null) {
				instance = new ChatManager();
			}
		}
		return instance;
	}
	
	public void privateChat(long receiverId, String content) {
		Player receiver = PlayerManager.getInstance().get(receiverId);
		if (receiver == null) {
			return;
		}
		if (!PlayerManager.getInstance().isOnline(receiverId)) {
			return;
		}
		TextChatMessage textMsg = new TextChatMessage();
		textMsg.setChannelType(ChannelType.PRIVATE.getType());
		textMsg.setReceiverId(receiverId);
		textMsg.setText(content);
		ChatChannel.getChannel(ChannelType.PRIVATE).send(textMsg);
	}
	
}
