package jforgame.server.game.cross.ladder.controller;

import jforgame.server.game.cross.ladder.message.ReqLadderApply;
import jforgame.server.game.cross.ladder.service.LadderClientManager;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;

/**
 * 天梯游戏服消息处理
 *
 */
@Controller
public class LadderClientController {

	@RequestMapping
	public void reqApply(long playerId, ReqLadderApply req) {
		LadderClientManager.getInstance().apply(playerId);
	}
}
