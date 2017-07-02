package com.kingston.game.login;

import org.apache.mina.core.session.IoSession;

import com.kingston.game.login.message.ResLoginMessage;
import com.kingston.net.MessagePusher;

public enum LoginManager {
	
	INSTANCE;
	
	/**
	 * 
	 * @param accoundId 账号流水号
	 * @param password  账号密码
	 */
	public void handleAccountLogin(IoSession session, long accoundId, String password) {
		if ("kingston".equals(password)) {
			MessagePusher.pushMessage(session, 
					new ResLoginMessage(LoginDataPool.LOGIN_SUCC, "登录成功"));
		} else {
			MessagePusher.pushMessage(session, 
					new ResLoginMessage(LoginDataPool.LOGIN_FAIL, "登录失败"));
		}
	}
	

}
