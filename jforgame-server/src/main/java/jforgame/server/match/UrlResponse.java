package jforgame.server.match;

public class UrlResponse {
	/** 执行结果状态码 */
	private byte code;
	/** 消息 */
	private String message;
	/** 额外参数 */
	private String attachment;

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
