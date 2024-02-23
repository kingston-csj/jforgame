package jforgame.demo.game.chat.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.chat.ChatDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module = Modules.CHAT, cmd = ChatDataPool.REQ_PRIVATE_CHAT)
public class ReqPrivateChat implements Message {
	
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
