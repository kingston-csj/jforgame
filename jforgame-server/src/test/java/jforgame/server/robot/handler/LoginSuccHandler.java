package jforgame.server.robot.handler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import jforgame.server.game.login.message.req.ReqSelectPlayer;
import jforgame.server.game.login.message.res.ResAccountLogin;
import jforgame.server.game.login.message.vo.PlayerLoginVo;
import jforgame.server.game.player.message.req.ReqCreateNewPlayer;
import jforgame.server.robot.MessageHandler;
import jforgame.server.robot.RobotSession;
import jforgame.socket.message.Message;

public class LoginSuccHandler implements MessageHandler {

	@Override
	public void onMessageReceive(RobotSession session, Message message) {
		ResAccountLogin loginMessage = (ResAccountLogin)message;
		List<PlayerLoginVo> players = loginMessage.getPlayers();
		// 账号无角色，则创角
		if (CollectionUtils.isEmpty(players)) {
			ReqCreateNewPlayer reqCreate = new ReqCreateNewPlayer();
			String name = "robot_" + session.getPlayer().getAccountId();
			reqCreate.setName(name);
			session.sendMessage(reqCreate);
		} else {
			PlayerLoginVo vo = players.get(0);
			ReqSelectPlayer reqSelect = new ReqSelectPlayer();
			reqSelect.setPlayerId(vo.getId());
			session.sendMessage(reqSelect);
		}
	}
}
