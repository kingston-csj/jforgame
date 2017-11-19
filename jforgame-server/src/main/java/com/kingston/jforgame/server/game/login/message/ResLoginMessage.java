package com.kingston.jforgame.server.game.login.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.net.socket.annotation.MessageMeta;
import com.kingston.jforgame.net.socket.message.Message;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.login.LoginDataPool;

@MessageMeta(module=Modules.LOGIN, cmd=LoginDataPool.RES_LOGIN)
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
