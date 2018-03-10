package com.kingston.jforgame.server.match.ladder.message;

import com.kingston.jforgame.net.socket.message.Message;


public class MResLadderApplySuccMessage extends Message {

	private byte code;

	public byte getCode() {
		return code;
	}

	public void setCode(byte code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "MResLadderApplySuccMessage [code=" + code + "]";
	}

}
