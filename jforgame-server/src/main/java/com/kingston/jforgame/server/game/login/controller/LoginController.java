package com.kingston.jforgame.server.game.login.controller;

import com.kingston.jforgame.server.game.login.LoginManager;
import com.kingston.jforgame.server.game.login.message.req.ReqAccountLogin;
import com.kingston.jforgame.server.game.login.message.req.ReqSelectPlayer;
import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping
	public void reqAccountLogin(IdSession session, ReqAccountLogin request) {
		LoginManager.getInstance().handleAccountLogin(session, request.getAccountId(), request.getPassword());
	}

	@RequestMapping
	public void reqSelectPlayer(IdSession session, ReqSelectPlayer requst) {
		LoginManager.getInstance().handleSelectPlayer(session, requst.getPlayerId());
	}

}
