package com.kingston.jforgame.match.game.ladder.message;

import com.kingston.jforgame.socket.message.Message;

public class Req_G2M_LadderMatchResult extends Message {
	
	private int serverId;

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
}
