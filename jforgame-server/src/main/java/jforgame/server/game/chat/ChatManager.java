package jforgame.server.game.chat;

import jforgame.server.game.GameContext;
import jforgame.server.game.chat.channel.ChannelType;
import jforgame.server.game.chat.channel.ChatChannel;
import jforgame.server.game.chat.model.TextChatMessage;
import jforgame.server.game.database.user.PlayerEnt;

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
