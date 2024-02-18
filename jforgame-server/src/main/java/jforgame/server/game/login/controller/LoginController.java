package jforgame.server.game.login.controller;

import jforgame.server.game.GameContext;
import jforgame.server.game.login.message.req.ReqAccountLogin;
import jforgame.server.game.login.message.req.ReqSelectPlayer;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestMapping;

@MessageRoute
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
