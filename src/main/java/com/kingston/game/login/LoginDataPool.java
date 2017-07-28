package com.kingston.game.login;

public class LoginDataPool {
	
	//cmd请求协议枚举
	/** 请求－登录 */
	public static final int REQ_LOGIN = 1;
	/** 请求－选择角色 */
	public static final int REQ_SELECT_PLAYER = 2;
	
	//cmd响应协议枚举
	/** 响应－登录 */
	public static final int RES_LOGIN = 501;
	
	/** 登录失败标识 */
	public static final int LOGIN_FAIL = 0;
	/** 登录成功标识 */
	public static final int LOGIN_SUCC = 1;
	

}
