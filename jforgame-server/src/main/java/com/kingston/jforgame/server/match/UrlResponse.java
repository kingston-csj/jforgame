package com.kingston.jforgame.server.match;

public class UrlResponse {
	/** 执行结果状态码 */
	private byte code;
	/** 消息 */
	private String message;
	/**额外消息 */
	private String attachemt;

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

	public String getAttachemt() {
		return attachemt;
	}

	public void setAttachemt(String attachemt) {
		this.attachemt = attachemt;
	}

	@Override
	public String toString() {
		return "UrlResponse [code=" + code + ", message=" + message + ", attachemt=" + attachemt + "]";
	}
}
