package jforgame.server.game.skill.facade;

import jforgame.server.game.skill.message.ReqUseSkill;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;

@MessageRoute
public class SkillController {

	@RequestHandler
	public void reqUseSkill(long playerId, ReqUseSkill request) {
	}

}
