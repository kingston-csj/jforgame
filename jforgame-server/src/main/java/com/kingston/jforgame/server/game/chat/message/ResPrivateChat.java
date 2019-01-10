package com.kingston.jforgame.server.game.chat.message;

import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.chat.ChatDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module = Modules.CHAT, cmd = ChatDataPool.RES_PRIVATE_CHAT)
public class ResPrivateChat extends Message {

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
