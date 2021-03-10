package jforgame.server.game.chat.channel;

public enum ChannelType {
	
	/**
	 * 世界聊天
	 */
	WORLD((byte)0),
	
	/**
	 * 个人私聊
	 */
	PRIVATE((byte)3),
	
	;
	
	byte type;
	
	ChannelType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}
	
}
