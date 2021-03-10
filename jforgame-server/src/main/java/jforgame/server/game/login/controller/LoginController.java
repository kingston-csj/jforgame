package jforgame.server.game.login.controller;

import jforgame.server.game.GameContext;
import jforgame.server.game.login.message.req.ReqAccountLogin;
import jforgame.server.game.login.message.req.ReqSelectPlayer;
import jforgame.socket.IdSession;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping
	public void reqAccountLogin(IdSession session, ReqAccountLogin request) {
        GameContext.loginManager.handleAccountLogin(session, request.getAccountId(), request.getPassword());
	}

	@RequestMapping
	public void reqSelectPlayer(IdSession session, ReqSelectPlayer requst) {
        GameContext.loginManager.handleSelectPlayer(session, requst.getPlayerId());
	}

}
