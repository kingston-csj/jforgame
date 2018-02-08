package com.kingston.jforgame.server.game.login.facade;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.net.socket.annotation.Controller;
import com.kingston.jforgame.net.socket.annotation.RequestMapping;
import com.kingston.jforgame.server.game.login.LoginManager;
import com.kingston.jforgame.server.game.login.message.ReqLoginMessage;
import com.kingston.jforgame.server.game.login.message.ReqSelectPlayerMessage;

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
