package com.kingston.jforgame.server.game.chat;

import com.kingston.jforgame.server.game.GameContext;
import com.kingston.jforgame.server.game.chat.channel.ChannelType;
import com.kingston.jforgame.server.game.chat.channel.ChatChannel;
import com.kingston.jforgame.server.game.chat.model.TextChatMessage;
import com.kingston.jforgame.server.game.database.user.player.PlayerEnt;

public class ChatManager {
	
	public void privateChat(long receiverId, String content) {
        PlayerEnt receiver = GameContext.playerManager.get(receiverId);
		if (receiver == null) {
			return;
		}
        if (!GameContext.playerManager.isOnline(receiverId)) {
			return;
		}
		TextChatMessage textMsg = new TextChatMessage();
		textMsg.setChannelType(ChannelType.PRIVATE.getType());
		textMsg.setReceiverId(receiverId);
		textMsg.setText(content);
		ChatChannel.getChannel(ChannelType.PRIVATE).send(textMsg);
	}
	
}
