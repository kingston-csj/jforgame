package com.kingston.jforgame.server.game.chat.message;

import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.chat.ChatDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

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
