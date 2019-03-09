package com.kingston.jforgame.server.game.cross.ladder.facade;

import com.kingston.jforgame.server.game.cross.ladder.message.ReqLadderApply;
import com.kingston.jforgame.server.game.cross.ladder.service.LadderClientManager;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

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
