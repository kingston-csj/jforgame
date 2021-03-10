package jforgame.match.game.ladder.message;

import jforgame.socket.message.Message;

public class Req_G2M_LadderMatchResult extends Message {
	
	private int serverId;

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
}
