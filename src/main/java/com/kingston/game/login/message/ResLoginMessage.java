package com.kingston.game.login.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.game.login.LoginDataPool;
import com.kingston.net.Message;
import com.kingston.net.Modules;
import com.kingston.net.annotation.Protocol;

@Protocol(module=Modules.LOGIN, cmd=LoginDataPool.CMD_RES_LOGIN)
public class ResLoginMessage extends Message {

	@Protobuf(order = 1)
	private int code;
	@Protobuf(order = 2)
	private String tips;
	
	public ResLoginMessage() {
		
	}
	
	public ResLoginMessage(int resultCode, String tips) {
		this.code = resultCode;
		this.tips = tips;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	@Override
	public String toString() {
		return "ResLoginMessage [code=" + code + ", tips=" + tips + "]";
	}
	
}
