package com.kingston.jforgame.server.match.ladder.message;

import com.kingston.jforgame.server.match.MatchMessage;


public class MResLadderApplySuccMessage extends MatchMessage {

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
