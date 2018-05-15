package com.kingston.jforgame.server.game.login.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.login.LoginDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

/**
 * 请求－账号登录
 * @author kingston
 */
@MessageMeta(module=Modules.LOGIN, cmd=LoginDataPool.REQ_LOGIN)
public class ReqLoginMessage extends Message {
	
	/** 账号流水号 */
	@Protobuf(order = 1)
	private long accountId;
	
	@Protobuf(order = 2)
	private String password;

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long playerId) {
		this.accountId = playerId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "ReqLoginMessage [accountId=" + accountId + ", password="
				+ password + "]";
	}
	
}
