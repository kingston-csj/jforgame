package com.kingston.game.gm.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.game.Modules;
import com.kingston.game.gm.GmConstant;
import com.kingston.net.Message;
import com.kingston.net.annotation.Protocol;

/**
 * gm执行结果
 * @author kingston
 */
@Protocol(module=Modules.LOGIN, cmd=GmConstant.RES_GM_RESULT)
public class ResGmResultMessage extends Message {
	
	/** 执行失败 */
	public static final byte FAIL = 0;
	/** 执行成功 */
	public static final byte SUCC = 1;
	
	@Protobuf(order = 1)
	private byte result;
	@Protobuf(order = 2)
	private String message;
	
	private ResGmResultMessage(byte result, String message) {
		this.result  = result;
		this.message = message;
	}
	
	public static ResGmResultMessage buildSuccResult(String msg) {
		return new ResGmResultMessage(SUCC, msg);
	}
	
	public static ResGmResultMessage buildFailResult(String msg) {
		return new ResGmResultMessage(FAIL, msg);
	}

	public byte getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}
	
}
