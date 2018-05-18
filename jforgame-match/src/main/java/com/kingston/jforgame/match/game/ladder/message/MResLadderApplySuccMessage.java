package com.kingston.jforgame.match.game.ladder.message;

import com.kingston.jforgame.common.Constants;
import com.kingston.jforgame.socket.message.Message;

/**
 * 天梯报名成功
 * @author kingston
 */
public class MResLadderApplySuccMessage extends Message {

	private byte code;

	public static MResLadderApplySuccMessage valueOfSucc() {
		MResLadderApplySuccMessage response = new MResLadderApplySuccMessage();
		response.code = Constants.SUCC;
		return response;
	}

	public static MResLadderApplySuccMessage valueOfFailed() {
		MResLadderApplySuccMessage response = new MResLadderApplySuccMessage();
		response.code = Constants.FAILED;;
		return response;
	}

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
