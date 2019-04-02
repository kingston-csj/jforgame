package com.kingston.jforgame.server.game.gm.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.gm.GmConstant;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

/**
 * gm执行结果
 * @author kingston
 */
@MessageMeta(module=Modules.GM, cmd=GmConstant.RES_GM_RESULT)
public class ResGmResult extends Message {
	
	/** 执行失败 */
	public static final byte FAIL = 0;
	/** 执行成功 */
	public static final byte SUCC = 1;
	
	@Protobuf(order = 1)
	private int result;
	@Protobuf(order = 2)
	private String message;
	
	public ResGmResult() {
		super();
	}

	private ResGmResult(byte result, String message) {
		this.result  = result;
		this.message = message;
	}
	
	public static ResGmResult buildSuccResult(String msg) {
		return new ResGmResult(SUCC, msg);
	}
	
	public static ResGmResult buildFailResult(String msg) {
		return new ResGmResult(FAIL, msg);
	}

	public int getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "ResGmResultMessage [result=" + result + ", message="
						+ message + "]";
	}
	
}
