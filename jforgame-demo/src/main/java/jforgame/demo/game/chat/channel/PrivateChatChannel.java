package jforgame.demo.game.chat.channel;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.chat.message.ResPrivateChat;
import jforgame.demo.game.chat.model.BaseChatMessage;
import jforgame.demo.game.chat.model.TextChatMessage;
import jforgame.demo.game.core.MessagePusher;
import jforgame.demo.game.database.user.PlayerEnt;

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
