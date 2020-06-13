package com.kingston.jforgame.server.game.login;

import com.kingston.jforgame.server.game.GameContext;
import com.kingston.jforgame.server.game.accout.entity.Account;
import com.kingston.jforgame.server.game.core.MessagePusher;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.gm.message.ResGmResult;
import com.kingston.jforgame.server.game.login.message.res.ResAccountLogin;
import com.kingston.jforgame.server.game.login.message.vo.PlayerLoginVo;
import com.kingston.jforgame.server.game.player.model.AccountProfile;
import com.kingston.jforgame.server.game.player.model.PlayerProfile;
import com.kingston.jforgame.server.game.scene.message.ResPlayerEnterScene;
import com.kingston.jforgame.server.net.SessionProperties;
import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.combine.CombineMessage;
import com.kingston.jforgame.socket.session.SessionManager;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginManager {


	/**
	 *
	 * @param accountId 账号流水号
	 * @param password  账号密码
	 */
	public void handleAccountLogin(IdSession session, long accountId, String password) {
		Account account = GameContext.getAccountManager().getOrCreate(accountId);
		session.setAttribute(SessionProperties.ACCOUNT, accountId);
		
		List<PlayerLoginVo> players = new ArrayList<>();
		AccountProfile accountProfile = GameContext.getPlayerManager().getAccountProfiles(accountId);
		List<PlayerProfile> playerProfiles = accountProfile.getPlayers();
		
		if (CollectionUtils.isNotEmpty(playerProfiles)) {
			for (PlayerProfile playerProfile : playerProfiles) {
				PlayerLoginVo vo = new PlayerLoginVo();
				vo.setId(playerProfile.getId());
				vo.setName(playerProfile.getName());
				players.add(vo);
			}
		}
		
		ResAccountLogin loginMessage = new ResAccountLogin();
		loginMessage.setPlayers(players);
		MessagePusher.pushMessage(session, loginMessage);
		
		if ("kingston".equals(password)) {
			CombineMessage combineMessage = new CombineMessage();
			combineMessage.addMessage(new ResPlayerEnterScene());
			combineMessage.addMessage(ResGmResult.buildSuccResult("执行gm成功"));
			MessagePusher.pushMessage(session, combineMessage);
		} 
	}

	/**
	 * 选角登录
	 * @param session
	 * @param playerId
	 */
	public void handleSelectPlayer(IdSession session, long playerId) {
		Player player = GameContext.getPlayerManager().get(playerId);
		if (player != null) {
			//绑定session与玩家id
			session.setAttribute(IdSession.ID, playerId);
			//加入在线列表
			GameContext.getPlayerManager().add2Online(player);
			SessionManager.INSTANCE.registerNewPlayer(playerId, session);
			//推送进入场景
			ResPlayerEnterScene response = new ResPlayerEnterScene();
			response.setMapId(1001);
			MessagePusher.pushMessage(session, response);

			//检查日重置
			GameContext.getPlayerManager().checkDailyReset(player);
		}
	}

}
