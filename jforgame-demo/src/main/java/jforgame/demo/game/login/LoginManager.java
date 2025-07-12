package jforgame.demo.game.login;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.accout.entity.AccountEnt;
import jforgame.demo.game.core.MessagePusher;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.gm.message.ResGmResult;
import jforgame.demo.game.login.message.res.ResAccountLogin;
import jforgame.demo.game.login.message.vo.PlayerLoginVo;
import jforgame.demo.game.player.model.AccountProfile;
import jforgame.demo.game.player.model.PlayerProfile;
import jforgame.demo.game.scene.message.ResPlayerEnterScene;
import jforgame.demo.socket.SessionManager;
import jforgame.socket.share.IdSession;
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
        AccountEnt account = GameContext.accountManager.getOrCreate(accountId);
		List<PlayerLoginVo> players = new ArrayList<>();
		AccountProfile accountProfile = GameContext.playerManager.getAccountProfiles(accountId);
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
		
		if ("kinson".equals(password)) {
			MessagePusher.pushMessage(session,new ResPlayerEnterScene());
			MessagePusher.pushMessage(session,ResGmResult.buildSuccResult("执行gm成功"));
		}
	}

	/**
	 * 选角登录
	 * @param session
	 * @param playerId
	 */
	public void handleSelectPlayer(IdSession session, long playerId) {
		PlayerEnt player = GameContext.playerManager.get(playerId);
		if (player != null) {
			//绑定session与玩家id
			session.setAttribute(IdSession.ID, playerId);
			//加入在线列表
			GameContext.playerManager.add2Online(player);

			player.setLevel(777);
			GameContext.playerManager.save(player);

			SessionManager.INSTANCE.registerNewPlayer(playerId, session);

			//推送进入场景
			ResPlayerEnterScene response = new ResPlayerEnterScene();
			response.setMapId(1001);
			MessagePusher.pushMessage(session, response);

			//检查日重置
			GameContext.playerManager.checkDailyReset(player);
		}
	}

}
