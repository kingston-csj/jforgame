package com.kingston.game.login;

import org.apache.mina.core.session.IoSession;

import com.kingston.game.login.message.ReqLoginMessage;
import com.kingston.net.annotation.Controller;
import com.kingston.net.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping
	public void handleAccountLogin(IoSession session, ReqLoginMessage request) {
		LoginManager.INSTANCE.handleAccountLogin(session, request.getAccountId(), request.getPassword());
	}
	
	
}
