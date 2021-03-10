package jforgame.match.game.ladder.message;

import jforgame.match.util.Constants;
import jforgame.socket.message.Message;

/**
 * 天梯报名成功
 * @author kinson
 */
public class Res_M2GLadderApplySucc extends Message {

	private byte code;

	public static Res_M2GLadderApplySucc valueOfSucc() {
		Res_M2GLadderApplySucc response = new Res_M2GLadderApplySucc();
		response.code = Constants.SUCC;
		return response;
	}

	public static Res_M2GLadderApplySucc valueOfFailed() {
		Res_M2GLadderApplySucc response = new Res_M2GLadderApplySucc();
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
