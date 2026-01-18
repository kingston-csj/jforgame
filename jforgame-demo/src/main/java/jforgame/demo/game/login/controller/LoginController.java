package jforgame.demo.game.login.controller;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.login.message.ReqAccountLogin;
import jforgame.demo.game.login.message.ReqSelectPlayer;
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
