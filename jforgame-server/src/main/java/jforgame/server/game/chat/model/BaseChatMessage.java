package jforgame.server.game.chat.model;

public abstract class BaseChatMessage {

	protected byte channelType;
	
	protected long senderId;
	
	protected long receiverId;

	public byte getChannelType() {
		return channelType;
	}

	public void setChannelType(byte channelType) {
		this.channelType = channelType;
	}

	public long getSenderId() {
		return senderId;
	}

	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}

	public long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(long receiverId) {
		this.receiverId = receiverId;
	}
	
}
