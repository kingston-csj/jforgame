package com.kingston.jforgame.server.robot.handler;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.kingston.jforgame.server.game.login.message.req.ReqSelectPlayerMessage;
import com.kingston.jforgame.server.game.login.message.res.ResLoginMessage;
import com.kingston.jforgame.server.game.login.message.vo.PlayerLoginVo;
import com.kingston.jforgame.server.game.player.message.ReqCreateNewPlayerMessage;
import com.kingston.jforgame.server.robot.MessageHandler;
import com.kingston.jforgame.server.robot.RobotSession;
import com.kingston.jforgame.socket.message.Message;

public class LoginSuccHandler implements MessageHandler {

	@Override
	public void onMessageReceive(RobotSession session, Message message) {
		ResLoginMessage loginMessage = (ResLoginMessage)message;
		List<PlayerLoginVo> players = loginMessage.getPlayers();
		// 账号无角色，则创角
		if (CollectionUtils.isEmpty(players)) {
			ReqCreateNewPlayerMessage reqCreate = new ReqCreateNewPlayerMessage();
			String name = "robot_" + session.getPlayer().getAccountId();
			reqCreate.setName(name);
			session.sendMessage(reqCreate);
		} else {
			PlayerLoginVo vo = players.get(0);
			ReqSelectPlayerMessage reqSelect = new ReqSelectPlayerMessage();
			reqSelect.setPlayerId(vo.getId());
			session.sendMessage(reqSelect);
		}
	}
}
