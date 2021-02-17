package com.kingston.jforgame.server.game.cross.ladder.message;

import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.cross.ladder.service.LadderDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module = Modules.CROSS_BUSINESS, cmd = LadderDataPool.RES_LADDER_LOGIN)
public class ReqLadderLogin extends Message {
	
	private String sign;

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
