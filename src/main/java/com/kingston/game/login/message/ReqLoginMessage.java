package com.kingston.game.login.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.game.login.LoginDataPool;
import com.kingston.net.Message;
import com.kingston.net.Modules;
import com.kingston.net.annotation.Protocol;

@Protocol(module = Modules.LOGIN, cmd=LoginDataPool.CMD_REQ_LOGIN)
public class ReqLoginMessage extends Message {
	
	@Protobuf
	private long playerId;
	
	@Protobuf
	private String password;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "ReqLoginMessage [playerId=" + playerId + ", password="
				+ password + "]";
	}
	
}
