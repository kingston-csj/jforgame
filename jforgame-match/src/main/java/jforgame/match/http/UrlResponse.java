package jforgame.match.http;

import jforgame.match.util.Constants;

public class UrlResponse {
	/** 执行结果状态码 */
	private byte code;
	/** 消息 */
	private String message;
	/**额外消息 */
	private String attachment;

	public static UrlResponse valueOfSucc() {
		UrlResponse response = new UrlResponse();
		response.code = Constants.SUCC;
		response.message = "执行成功";
		return response;
	}

	public static UrlResponse valueOfFailed() {
		UrlResponse response = new UrlResponse();
		response.code = Constants.FAILED;
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

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "UrlResponse [code=" + code + ", message=" + message + ", attachemt=" + attachment + "]";
	}
}
