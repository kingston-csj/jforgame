package jforgame.demo.game.chat.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.chat.ChatDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module = Modules.CHAT, cmd = ChatDataPool.RES_PRIVATE_CHAT)
public class ResPrivateChat implements Message {

	private long senderId;

	private String content;

	public long getSenderId() {
		return senderId;
	}

	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
