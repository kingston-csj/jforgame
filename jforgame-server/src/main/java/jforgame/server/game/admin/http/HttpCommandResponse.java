package jforgame.server.game.admin.http;

/**
 * @author kinson
 */
public class HttpCommandResponse {
	/**  执行成功 */
	public static final byte SUCC = 1;
	/**  执行失败 */
	public static final byte FAILED = 2;
	/** 执行结果状态码 */
	private byte code;
	/** 额外消息 */
	private String message;
	
	public static HttpCommandResponse valueOfSucc() {
		HttpCommandResponse response = new HttpCommandResponse();
		response.code = SUCC;
		response.message = "执行成功";
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

	@Override
	public String toString() {
		return "HttpCommandResponse [code=" + code + ", message="
						+ message + "]";
	}

}
