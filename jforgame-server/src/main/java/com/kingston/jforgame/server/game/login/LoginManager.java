package com.kingston.jforgame.server.game.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.kingston.jforgame.server.game.accout.entity.Account;
import com.kingston.jforgame.server.game.accout.entity.AccountManager;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.gm.message.ResGmResultMessage;
import com.kingston.jforgame.server.game.login.message.res.ResLoginMessage;
import com.kingston.jforgame.server.game.login.message.vo.PlayerLoginVo;
import com.kingston.jforgame.server.game.player.PlayerManager;
import com.kingston.jforgame.server.game.player.model.AccountProfile;
import com.kingston.jforgame.server.game.player.model.PlayerProfile;
import com.kingston.jforgame.server.game.scene.message.ResPlayerEnterSceneMessage;
import com.kingston.jforgame.server.net.SessionProperties;
import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.combine.CombineMessage;
import com.kingston.jforgame.socket.message.MessagePusher;
import com.kingston.jforgame.socket.session.SessionManager;

public class LoginManager {

	private static volatile LoginManager instance = new LoginManager();

	private LoginManager() {}

	public static LoginManager getInstance() {
		return instance;
	}

	/**
	 *
	 * @param accoundId 账号流水号
	 * @param password  账号密码
	 */
	public void handleAccountLogin(IdSession session, long accountId, String password) {
		Account account = AccountManager.getInstance().getOrCreate(accountId);
		session.setAttribute(SessionProperties.ACCOUNT, accountId);
		
		List<PlayerLoginVo> players = new ArrayList<>();
		AccountProfile accountProfile = PlayerManager.getInstance().getAccountProfiles(accountId);
		List<PlayerProfile> playerProfiles = accountProfile.getPlayers();
		
		if (CollectionUtils.isNotEmpty(playerProfiles)) {
			for (PlayerProfile playerProfile : playerProfiles) {
				PlayerLoginVo vo = new PlayerLoginVo();
				vo.setId(playerProfile.getId());
				vo.setName(playerProfile.getName());
				players.add(vo);
			}
		}
		
		ResLoginMessage loginMessage = new ResLoginMessage();
		loginMessage.setPlayers(players);
		MessagePusher.pushMessage(session, loginMessage);
		
		if ("kingston".equals(password)) {
			CombineMessage combineMessage = new CombineMessage();
			combineMessage.addMessage(new ResPlayerEnterSceneMessage());
			combineMessage.addMessage(ResGmResultMessage.buildSuccResult("执行gm成功"));
			MessagePusher.pushMessage(session, combineMessage);
		} 
	}

	/**
	 * 选角登录
	 * @param session
	 * @param playerId
	 */
	public void handleSelectPlayer(IdSession session, long playerId) {
		Player player = PlayerManager.getInstance().get(playerId);
		if (player != null) {
			//绑定session与玩家id
			session.setAttribute(IdSession.ID, playerId);
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
