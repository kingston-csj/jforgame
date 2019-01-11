package com.kingston.jforgame.server.game.login.facade;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.server.game.login.LoginManager;
import com.kingston.jforgame.server.game.login.message.req.ReqLoginMessage;
import com.kingston.jforgame.server.game.login.message.req.ReqSelectPlayerMessage;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

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
