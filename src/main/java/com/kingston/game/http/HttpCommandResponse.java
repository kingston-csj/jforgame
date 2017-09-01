package com.kingston.game.http;

public class HttpCommandResponse {
	
	public static final byte SUCC = 1;
	
	public static final byte FAILED = 2;
	/** 执行结果状态码 */
	private byte code;
	/** 额外消息 */
	private String message;
	
	public static HttpCommandResponse valueOfSucc() {
		HttpCommandResponse response = new HttpCommandResponse();
		response.code = SUCC;
		return response;
	}
	
	public static HttpCommandResponse valueOfFailed() {
		HttpCommandResponse response = new HttpCommandResponse();
		response.code = FAILED;
		return response;
	}

	public byte getCode() {
		return code;
	}

	public void setCode(byte code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
