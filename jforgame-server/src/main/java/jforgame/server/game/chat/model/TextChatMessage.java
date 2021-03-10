package jforgame.server.game.chat.model;

/**
 * 文本聊天消息
 *
 */
public class TextChatMessage extends BaseChatMessage {
	
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
