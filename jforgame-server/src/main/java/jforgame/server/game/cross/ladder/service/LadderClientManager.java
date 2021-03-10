package jforgame.server.game.cross.ladder.service;

import java.io.IOException;

import jforgame.server.ServerConfig;
import jforgame.server.cross.core.client.C2SSessionPoolFactory;
import jforgame.server.cross.core.client.CCSession;
import jforgame.server.match.MatchHttpUtil;
import jforgame.server.game.GameContext;
import jforgame.server.game.cross.ladder.message.G2F_LadderTransfer;
import jforgame.server.game.cross.ladder.message.G2M_LadderApply;
import jforgame.server.game.cross.ladder.message.vo.LadderMatchVo;
import jforgame.server.game.cross.ladder.utils.CrossJsonUtil;
import jforgame.server.game.database.user.PlayerEnt;
import jforgame.server.logs.LoggerUtils;

/**
 * 天梯游戏服（客户端）业务处理
 *
 */
public class LadderClientManager {

	private static volatile LadderClientManager self = new LadderClientManager();
	
	public static LadderClientManager getInstance() {
		return self;
	}
	
	public void init() {
		ServerConfig config = ServerConfig.getInstance();
		if (!config.isFight()) {
			return;
		}
	}
	
	
	public void apply(long playerId) {
        PlayerEnt player = GameContext.playerManager.get(playerId);
		// 一堆业务判断
		G2M_LadderApply apply = new G2M_LadderApply();
		apply.setPlayerId(playerId);
		apply.setPower(100);
		apply.setScore(100);
		try {
			MatchHttpUtil.submit(apply);
		} catch (IOException e) {
			LoggerUtils.error("天梯报错异常，玩家:" + playerId, e);
			return;
		}
	}
	
	private void handleFight(LadderMatchVo matchVo) {
		int selfServerId = ServerConfig.getInstance().getServerId();
		if (matchVo.getBlueServerId() == selfServerId) {
            PlayerEnt player = GameContext.playerManager.get(matchVo.getBluePlayerId());
			transferToFight(player, matchVo);
		}
		if (matchVo.getRedServerId() == selfServerId) {
            PlayerEnt player = GameContext.playerManager.get(matchVo.getRedPlayerId());
			transferToFight(player, matchVo);
		}
	}
	
	/**
	 * 将玩家从游戏服传到战斗服
	 * @param player
	 */
	private void transferToFight(PlayerEnt player, LadderMatchVo matchVo) {
		String fightIp = matchVo.getFightServerIp();
		int fightPort = matchVo.getFightServerPort();
		
		CCSession session = C2SSessionPoolFactory.getInstance().borrowSession(fightIp, fightPort);
		if (session != null) {
			// 将玩家数据打包后发到战斗服
			G2F_LadderTransfer rpcTransfer = new G2F_LadderTransfer();
			String playerJson = CrossJsonUtil.object2String(player);
			session.sendMessage(rpcTransfer);
			// 通知客户端切换socket到战斗服
		}
	}
	
}
