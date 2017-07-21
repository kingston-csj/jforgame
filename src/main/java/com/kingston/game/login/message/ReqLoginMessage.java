package com.kingston.game.login.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.game.Modules;
import com.kingston.game.login.LoginDataPool;
import com.kingston.net.Message;
import com.kingston.net.annotation.Protocol;

/**
 * 请求－账号登录
 * @author kingston
 */
@Protocol(module=Modules.LOGIN, cmd=LoginDataPool.CMD_REQ_LOGIN)
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
