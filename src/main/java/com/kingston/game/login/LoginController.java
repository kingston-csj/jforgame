package com.kingston.game.login;

import org.apache.mina.core.session.IoSession;

import com.kingston.game.login.message.ReqLoginMessage;
import com.kingston.game.login.message.ReqSelectPlayerMessage;
import com.kingston.net.annotation.Controller;
import com.kingston.net.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping
	public void reqAccountLogin(IoSession session, ReqLoginMessage request) {
		LoginManager.getInstance().handleAccountLogin(session, request.getAccountId(), request.getPassword());
	}
	
	@RequestMapping
	public void reqSelectPlayer(IoSession session, ReqSelectPlayerMessage requst) {
		LoginManager.getInstance().handleSelectPlayer(session, requst.getPlayerId());
	}
	
}
