package jforgame.server.game.login.controller;

import jforgame.server.game.GameContext;
import jforgame.server.game.login.message.req.ReqAccountLogin;
import jforgame.server.game.login.message.req.ReqSelectPlayer;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;

@MessageRoute
public class LoginController {

	@RequestHandler
	public void reqAccountLogin(IdSession session, ReqAccountLogin request) {
        GameContext.loginManager.handleAccountLogin(session, request.getAccountId(), request.getPassword());
	}

	@RequestHandler
	public void reqSelectPlayer(IdSession session, ReqSelectPlayer request) {
        GameContext.loginManager.handleSelectPlayer(session, request.getPlayerId());
	}

}
