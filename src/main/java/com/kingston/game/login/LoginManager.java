package com.kingston.game.login;

import org.apache.mina.core.session.IoSession;

import com.kingston.game.database.user.player.Player;
import com.kingston.game.gm.message.ResGmResultMessage;
import com.kingston.game.login.message.ResLoginMessage;
import com.kingston.game.player.PlayerManager;
import com.kingston.game.scene.message.ResPlayerEnterSceneMessage;
import com.kingston.net.combine.CombineMessage;
import com.kingston.net.message.Message;
import com.kingston.net.message.MessagePusher;
import com.kingston.net.session.SessionManager;
import com.kingston.net.session.SessionProperties;

public class LoginManager {

	private static LoginManager instance = new LoginManager();

	private LoginManager() {}

	public static LoginManager getInstance() {
		return instance;
	}

	/**
	 *
	 * @param accoundId 账号流水号
	 * @param password  账号密码
	 */
	public void handleAccountLogin(IoSession session, long accoundId, String password) {
		if ("kingston".equals(password)) {
			Message response  = new ResLoginMessage(LoginDataPool.LOGIN_SUCC, "登录成功");

			CombineMessage combineMessage = new CombineMessage();
			combineMessage.addMessage(response);
			combineMessage.addMessage(new ResPlayerEnterSceneMessage());
			combineMessage.addMessage(ResGmResultMessage.buildSuccResult("执行gm成功"));
			MessagePusher.pushMessage(session, combineMessage);
		} else {
			MessagePusher.pushMessage(session,
					new ResLoginMessage(LoginDataPool.LOGIN_FAIL, "登录失败"));
		}
	}

	/**
	 * 选角登录
	 * @param session
	 * @param playerId
	 */
	public void handleSelectPlayer(IoSession session, long playerId) {
		Player player = PlayerManager.getInstance().get(playerId);
		if (player != null) {
			//绑定session与玩家id
			session.setAttribute(SessionProperties.PLAYER_ID, playerId);
			//加入在线列表
			PlayerManager.getInstance().add2Online(player);
			SessionManager.INSTANCE.registerNewPlayer(playerId, session);
			//推送进入场景
			ResPlayerEnterSceneMessage response = new ResPlayerEnterSceneMessage();
			response.setMapId(1001);
			MessagePusher.pushMessage(session, response);


			//检查日重置
			PlayerManager.getInstance().checkDailyReset(player);

		}
	}

}
