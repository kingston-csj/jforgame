package com.kingston.game.login.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.game.Modules;
import com.kingston.game.login.LoginDataPool;
import com.kingston.net.Message;
import com.kingston.net.annotation.Protocol;

@Protocol(module=Modules.LOGIN, cmd=LoginDataPool.REQ_SELECT_PLAYER)
public class ReqSelectPlayerMessage extends Message {
	
	@Protobuf(order = 1)
	private long playerId;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	@Override
	public String toString() {
		return "ReqSelectPlayerMessage [playerId=" + playerId + "]";
	}
	
}
