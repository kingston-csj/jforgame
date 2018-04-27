package com.kingston.jforgame.admin.utils;

import com.kingston.jforgame.common.Constants;

/**
 * @author kingston
 */
public class HttpResult {
	
	/** status {@link Constants#SUCC} */
	private byte code;
	/** attachment */
	private String data;
	
	public static HttpResult valueOfSucc() {
		HttpResult result = new HttpResult();
		result.code = Constants.SUCC;
		return result;
	}
	
	public static HttpResult valueOfFailed() {
		HttpResult result = new HttpResult();
		result.code = Constants.FAILED;
		return result;
	}

	public byte getCode() {
		return code;
	}

	public void setCode(byte code) {
		this.code = code;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "HttpResult [code=" + code + ", data=" + data + "]";
	}
	
}
