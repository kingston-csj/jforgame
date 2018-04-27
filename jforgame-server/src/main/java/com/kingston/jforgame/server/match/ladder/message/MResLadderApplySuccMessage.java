package com.kingston.jforgame.server.match.ladder.message;

import com.kingston.jforgame.server.match.AbstractMatchMessage;


public class MResLadderApplySuccMessage extends AbstractMatchMessage {

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
