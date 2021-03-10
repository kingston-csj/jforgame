package jforgame.server.game.chat.channel;

import jforgame.server.game.GameContext;
import jforgame.server.game.chat.message.ResPrivateChat;
import jforgame.server.game.chat.model.BaseChatMessage;
import jforgame.server.game.chat.model.TextChatMessage;
import jforgame.server.game.core.MessagePusher;
import jforgame.server.game.database.user.PlayerEnt;

public class PrivateChatChannel extends ChatChannel {

	@Override
	public ChannelType getChannelType() {
		return ChannelType.PRIVATE;
	}
	
	@Override
	public boolean verifySend(BaseChatMessage message) {
		return true;
	}
	
	@Override
	public void doSend(BaseChatMessage message) {
		TextChatMessage textMessage = (TextChatMessage)message;
		long receiverId = message.getReceiverId();
        PlayerEnt receiver = GameContext.playerManager.get(receiverId);
		
		ResPrivateChat targetNotify = new ResPrivateChat();
		targetNotify.setSenderId(message.getSenderId());
		targetNotify.setContent("我很好");
		MessagePusher.pushMessage(receiverId, targetNotify);
	}

}
