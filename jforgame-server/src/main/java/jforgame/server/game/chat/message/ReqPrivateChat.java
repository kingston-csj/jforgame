package jforgame.server.game.chat.message;

import jforgame.server.game.Modules;
import jforgame.server.game.chat.ChatDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module = Modules.CHAT, cmd = ChatDataPool.REQ_PRIVATE_CHAT)
public class ReqPrivateChat extends Message {
	
	private long receiverId;
	
	private String content;

	public long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(long receiverId) {
		this.receiverId = receiverId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
