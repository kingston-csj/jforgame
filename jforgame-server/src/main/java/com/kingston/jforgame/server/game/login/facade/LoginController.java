package com.kingston.jforgame.server.game.login.facade;

import com.kingston.jforgame.server.game.login.LoginManager;
import com.kingston.jforgame.server.game.login.message.req.ReqLoginMessage;
import com.kingston.jforgame.server.game.login.message.req.ReqSelectPlayerMessage;
import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping
	public void reqAccountLogin(IdSession session, ReqLoginMessage request) {
		LoginManager.getInstance().handleAccountLogin(session, request.getAccountId(), request.getPassword());
	}

	@RequestMapping
	public void reqSelectPlayer(IdSession session, ReqSelectPlayerMessage requst) {
		LoginManager.getInstance().handleSelectPlayer(session, requst.getPlayerId());
	}

}
